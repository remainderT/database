package org.buaa.shortlink.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.buaa.shortlink.common.convention.exception.ClientException;
import org.buaa.shortlink.common.convention.exception.ServiceException;
import org.buaa.shortlink.common.enums.VailDateTypeEnum;
import org.buaa.shortlink.dao.entity.LinkAccessStatsDO;
import org.buaa.shortlink.dao.entity.LinkBrowserStatsDO;
import org.buaa.shortlink.dao.entity.LinkLocaleStatsDO;
import org.buaa.shortlink.dao.entity.LinkOsStatsDO;
import org.buaa.shortlink.dao.entity.LinkUipStatsDO;
import org.buaa.shortlink.dao.entity.LinkUvStatsDO;
import org.buaa.shortlink.dao.entity.ShortLinkDO;
import org.buaa.shortlink.dao.mapper.LinkAccessStatsMapper;
import org.buaa.shortlink.dao.mapper.LinkBrowserStatsMapper;
import org.buaa.shortlink.dao.mapper.LinkLocaleStatsMapper;
import org.buaa.shortlink.dao.mapper.LinkOsStatsMapper;
import org.buaa.shortlink.dao.mapper.LinkUipStatsDOMapper;
import org.buaa.shortlink.dao.mapper.LinkUvStatsDOMapper;
import org.buaa.shortlink.dao.mapper.ShortLinkMapper;
import org.buaa.shortlink.dto.req.ShortLinkCreateReqDTO;
import org.buaa.shortlink.dto.req.ShortLinkPageReqDTO;
import org.buaa.shortlink.dto.req.ShortLinkUpdateReqDTO;
import org.buaa.shortlink.dto.resp.ShortLinkCreateRespDTO;
import org.buaa.shortlink.dto.resp.ShortLinkPageRespDTO;
import org.buaa.shortlink.service.ShortLinkService;
import org.buaa.shortlink.toolkit.HashGenerator;
import org.buaa.shortlink.toolkit.LinkUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.buaa.shortlink.common.consts.ShortLinkConstants.AMAP_REMOTE_URL;
import static org.buaa.shortlink.common.enums.ServiceErrorCodeEnum.SHORT_LINK_EXPIRED;
import static org.buaa.shortlink.common.enums.ServiceErrorCodeEnum.SHORT_LINK_GENERATE_ERROR;
import static org.buaa.shortlink.common.enums.ServiceErrorCodeEnum.SHORT_LINK_STATS_RECORD_ERROR;
import static org.buaa.shortlink.common.enums.UserErrorCodeEnum.SHORT_LINK_NULL;

/**
 * 短链接接口实现层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    @Value("${short-link.domain.default}")
    private String createShortLinkDefaultDomain;
    @Value("${short-link.stats.locale.amap-key}")
    private String statsLocaleAmapKey;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkUvStatsDOMapper linkUvStatsDOMapper;
    private final LinkUipStatsDOMapper linkUipStatsDOMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortLinkSuffix = generateSuffix(requestParam);
        ShortLinkDO shortLinkDO = BeanUtil.toBean(requestParam, ShortLinkDO.class);
        shortLinkDO.setShortUri(shortLinkSuffix);
        shortLinkDO.setDomain(createShortLinkDefaultDomain);
        shortLinkDO.setFullShortUrl(shortLinkDO.getDomain() + "/" + shortLinkSuffix);
        shortLinkDO.setFavicon(getFavicon(requestParam.getOriginUrl()));
        baseMapper.insert(shortLinkDO);
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(shortLinkDO.getFullShortUrl())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        IPage<ShortLinkDO> page = baseMapper.selectPage(requestParam, queryWrapper);
        return page.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));
    }

    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getOriginGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO hasShortLinkDO = baseMapper.selectOne(queryWrapper);
        if (hasShortLinkDO == null) {
            throw new ClientException(SHORT_LINK_NULL);
        }
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0)
                .set(Objects.equals(requestParam.getValidDateType(), VailDateTypeEnum.PERMANENT.getType()), ShortLinkDO::getValidDate, null);
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(hasShortLinkDO.getDomain())
                .shortUri(hasShortLinkDO.getShortUri())
                // .favicon(Objects.equals(requestParam.getOriginUrl(), hasShortLinkDO.getOriginUrl()) ? hasShortLinkDO.getFavicon() : getFavicon(requestParam.getOriginUrl()))
                .createdType(hasShortLinkDO.getCreatedType())
                .gid(requestParam.getGid())
                .originUrl(requestParam.getOriginUrl())
                .describe(requestParam.getDescribe())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .build();
        baseMapper.update(shortLinkDO, updateWrapper);
    }

    private String generateSuffix(ShortLinkCreateReqDTO requestParam) {
        int customGenerateCount = 0;
        String originUrl = requestParam.getOriginUrl();
        String shortUri = HashGenerator.hashToBase62(originUrl);
        while (true) {
            if (customGenerateCount > 10) {
                throw new ServiceException(SHORT_LINK_GENERATE_ERROR);
            }
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getShortUri, shortUri);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);
            if (shortLinkDO == null) {
                return shortUri;
            }
            shortUri = HashGenerator.hashToBase62(originUrl + UUID.randomUUID().toString());
            customGenerateCount++;
        }
    }

    @SneakyThrows
    @Override
    public void restoreUrl(String shortUri, ServletRequest request, ServletResponse response) {
        String serverName = request.getServerName();
        String serverPort = Optional.of(request.getServerPort())
                .filter(each -> !Objects.equals(each, 80))
                .map(String::valueOf)
                .map(each -> ":" + each)
                .orElse("");
        String fullShortUrl = serverName + serverPort + "/" + shortUri;
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);
        if (shortLinkDO != null) {
            if (shortLinkDO.getValidDateType() == 1 && shortLinkDO.getValidDate().before(new Date())) {
                throw new ServiceException(SHORT_LINK_EXPIRED);
            }
            shortLinkStats(fullShortUrl, request, response);
            ((HttpServletResponse) response).sendRedirect(shortLinkDO.getOriginUrl());
        } else {
            ((HttpServletResponse) response).sendRedirect("/page/notfound");
        }
    }

    @SneakyThrows
    @Override
    public String getTitleByUrl(String url) {
        URL targetUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Document document = Jsoup.connect(url).get();
            return document.title();
        }
        return "Error while fetching title.";
    }

    @SneakyThrows
    private String getFavicon(String url) {
        URL targetUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (HttpURLConnection.HTTP_OK == responseCode) {
            Document document = Jsoup.connect(url).get();
            Element faviconLink = document.select("link[rel~=(?i)^(shortcut )?icon]").first();
            if (faviconLink != null) {
                return faviconLink.attr("abs:href");
            }
        }
        return null;
    }

    private void shortLinkStats(String fullShortUrl, ServletRequest request, ServletResponse response) {
        try {
            boolean isNewUv = checkNewUv(fullShortUrl, request, response);
            boolean isNewUip = checkNewUip(fullShortUrl, request);
            String ip = LinkUtil.getActualIp(((HttpServletRequest) request));
            int hour = DateUtil.hour(new Date(), true);
            Week week = DateUtil.dayOfWeekEnum(new Date());
            int weekValue = week.getIso8601Value();
            LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
                    .pv(1)
                    .uv(isNewUv ? 1 : 0)
                    .uip(isNewUip ? 1 : 0)
                    .hour(hour)
                    .weekday(weekValue)
                    .fullShortUrl(fullShortUrl)
                    .date(new Date())
                    .build();
            linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);
            // 获取地理位置信息
            Map<String, Object> localeParamMap = new HashMap<>();
            localeParamMap.put("key", statsLocaleAmapKey);
            localeParamMap.put("ip", ip);
            String localeResultStr = HttpUtil.get(AMAP_REMOTE_URL, localeParamMap);
            JSONObject localeResultObj = JSON.parseObject(localeResultStr);
            String infoCode = localeResultObj.getString("infocode");
            if (StrUtil.isNotBlank(infoCode) && StrUtil.equals(infoCode, "10000")) {
                String province = localeResultObj.getString("province");
                boolean unknownFlag = StrUtil.equals(province, "[]");
                LinkLocaleStatsDO linkLocaleStatsDO = LinkLocaleStatsDO.builder()
                        .province(unknownFlag ? "未知" : province)
                        .city(unknownFlag ? "未知" : localeResultObj.getString("city"))
                        .adcode(unknownFlag ? "未知" : localeResultObj.getString("adcode"))
                        .cnt(1)
                        .fullShortUrl(fullShortUrl)
                        .country("中国")
                        .date(new Date())
                        .build();
                linkLocaleStatsMapper.shortLinkLocaleState(linkLocaleStatsDO);
            // 获取操作系统信息
                LinkOsStatsDO linkOsStatsDO = LinkOsStatsDO.builder()
                        .os(LinkUtil.getOs(((HttpServletRequest) request)))
                        .cnt(1)
                        .fullShortUrl(fullShortUrl)
                        .date(new Date())
                        .build();
                linkOsStatsMapper.shortLinkOsState(linkOsStatsDO);
            // 获取浏览器信息
                LinkBrowserStatsDO linkBrowserStatsDO = LinkBrowserStatsDO.builder()
                        .browser(LinkUtil.getBrowser(((HttpServletRequest) request)))
                        .cnt(1)
                        .fullShortUrl(fullShortUrl)
                        .date(new Date())
                        .build();
                linkBrowserStatsMapper.shortLinkBrowserState(linkBrowserStatsDO);
            }
        } catch (Throwable ex) {
            throw new ServiceException(SHORT_LINK_STATS_RECORD_ERROR);
        }
    }

    public boolean checkNewUv(String fullShortUrl, ServletRequest request, ServletResponse response) {
        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        String uvCookieValue = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("uv".equals(cookie.getName())) {
                    uvCookieValue = cookie.getValue();
                    break;
                }
            }
        }
        boolean isNewUv = true;
        if (uvCookieValue != null) {
            LambdaQueryWrapper<LinkUvStatsDO> queryWrapper = Wrappers.lambdaQuery(LinkUvStatsDO.class)
                    .eq(LinkUvStatsDO::getUuid, uvCookieValue);
            isNewUv = linkUvStatsDOMapper.selectOne(queryWrapper) == null;
        }
        if (isNewUv) {
            String uuid = UUID.randomUUID().toString();
            LinkUvStatsDO newUvStats = LinkUvStatsDO.builder()
                    .uuid(uuid)
                    .fullShortUrl(fullShortUrl)
                    .build();
            linkUvStatsDOMapper.insert(newUvStats);
            Cookie uvCookie = new Cookie("uv", uuid);
            uvCookie.setMaxAge(60 * 60 * 24 * 30);
            uvCookie.setPath(StrUtil.sub(fullShortUrl, fullShortUrl.indexOf("/"), fullShortUrl.length()));
            ((HttpServletResponse) response).addCookie(uvCookie);
        }
        return isNewUv;
    }

    public boolean checkNewUip(String fullShortUrl, ServletRequest request) {
        String ip = LinkUtil.getActualIp(((HttpServletRequest) request));
        LambdaQueryWrapper<LinkUipStatsDO> queryWrapper = Wrappers.lambdaQuery(LinkUipStatsDO.class)
                .eq(LinkUipStatsDO::getIp, ip)
                .eq(LinkUipStatsDO::getFullShortUrl, fullShortUrl);
        LinkUipStatsDO linkUipStatsDO = linkUipStatsDOMapper.selectOne(queryWrapper);
        if (linkUipStatsDO == null) {
            linkUipStatsDO = LinkUipStatsDO.builder()
                    .ip(ip)
                    .fullShortUrl(fullShortUrl)
                    .build();
            linkUipStatsDOMapper.insert(linkUipStatsDO);
            return true;
        }
        return false;
    }

}
