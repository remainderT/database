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
import org.buaa.shortlink.common.biz.user.UserContext;
import org.buaa.shortlink.common.convention.exception.ClientException;
import org.buaa.shortlink.common.convention.exception.ServiceException;
import org.buaa.shortlink.common.enums.UserTypeEnum;
import org.buaa.shortlink.common.enums.VailDateTypeEnum;
import org.buaa.shortlink.dao.entity.LinkAccessLogsDO;
import org.buaa.shortlink.dao.entity.LinkAccessStatsDO;
import org.buaa.shortlink.dao.entity.LinkBrowserStatsDO;
import org.buaa.shortlink.dao.entity.LinkDeviceStatsDO;
import org.buaa.shortlink.dao.entity.LinkLocaleStatsDO;
import org.buaa.shortlink.dao.entity.LinkNetworkStatsDO;
import org.buaa.shortlink.dao.entity.LinkOsStatsDO;
import org.buaa.shortlink.dao.entity.LinkStatsTodayDO;
import org.buaa.shortlink.dao.entity.LinkUipStatsDO;
import org.buaa.shortlink.dao.entity.LinkUvStatsDO;
import org.buaa.shortlink.dao.entity.ShortLinkDO;
import org.buaa.shortlink.dao.mapper.LinkAccessLogsMapper;
import org.buaa.shortlink.dao.mapper.LinkAccessStatsMapper;
import org.buaa.shortlink.dao.mapper.LinkBrowserStatsMapper;
import org.buaa.shortlink.dao.mapper.LinkDeviceStatsMapper;
import org.buaa.shortlink.dao.mapper.LinkLocaleStatsMapper;
import org.buaa.shortlink.dao.mapper.LinkNetworkStatsMapper;
import org.buaa.shortlink.dao.mapper.LinkOsStatsMapper;
import org.buaa.shortlink.dao.mapper.LinkStatsTodayMapper;
import org.buaa.shortlink.dao.mapper.LinkUipStatsDOMapper;
import org.buaa.shortlink.dao.mapper.LinkUvStatsDOMapper;
import org.buaa.shortlink.dao.mapper.ShortLinkMapper;
import org.buaa.shortlink.dto.req.ShortLinkBatchCreateReqDTO;
import org.buaa.shortlink.dto.req.ShortLinkCreateReqDTO;
import org.buaa.shortlink.dto.req.ShortLinkPageReqDTO;
import org.buaa.shortlink.dto.req.ShortLinkUpdateReqDTO;
import org.buaa.shortlink.dto.resp.ShortLinkBatchCreateRespDTO;
import org.buaa.shortlink.dto.resp.ShortLinkCreateRespDTO;
import org.buaa.shortlink.dto.resp.ShortLinkPageRespDTO;
import org.buaa.shortlink.service.ShortLinkService;
import org.buaa.shortlink.toolkit.HashGenerator;
import org.buaa.shortlink.toolkit.ImageUpload;
import org.buaa.shortlink.toolkit.LinkUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.buaa.shortlink.common.consts.ShortLinkConstants.AMAP_REMOTE_URL;
import static org.buaa.shortlink.common.enums.ServiceErrorCodeEnum.SHORT_LINK_EXPIRED;
import static org.buaa.shortlink.common.enums.ServiceErrorCodeEnum.SHORT_LINK_GENERATE_ERROR;
import static org.buaa.shortlink.common.enums.UserErrorCodeEnum.SHORT_LINK_NULL;
import static org.buaa.shortlink.common.enums.UserErrorCodeEnum.USER_RIGHT_LIMIT;

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
    private final ShortLinkMapper shortLinkMapper;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkUvStatsDOMapper linkUvStatsDOMapper;
    private final LinkUipStatsDOMapper linkUipStatsDOMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;
    private final LinkStatsTodayMapper linkStatsTodayMapper;
    private final ImageUpload imageUpload;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortLinkSuffix = generateSuffix(requestParam.getOriginUrl());
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
    public ShortLinkCreateRespDTO createShortLinkByImage(@RequestParam("file") MultipartFile file, String gid, String describe) {
        if (!Objects.equals(UserContext.getIsVip(), UserTypeEnum.vip)) {
            throw new ServiceException(USER_RIGHT_LIMIT);
        }
        String ossUrl = imageUpload.uploadImage(file);
        String shortLinkSuffix = generateSuffix(ossUrl);
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .originUrl(ossUrl)
                .describe(describe)
                .gid(gid)
                .domain(createShortLinkDefaultDomain)
                .validDateType(0)
                .fullShortUrl(createShortLinkDefaultDomain + "/" + shortLinkSuffix)
                .createdType(0)
                .build();
        baseMapper.insert(shortLinkDO);
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(shortLinkDO.getFullShortUrl())
                .originUrl(ossUrl)
                .gid(gid)
                .build();
    }

    @Override
    public ShortLinkBatchCreateRespDTO batchCreateShortLink(ShortLinkBatchCreateReqDTO requestParam) {
        List<String> originUrls = requestParam.getOriginUrls();
        List<String> describes = requestParam.getDescribes();
        List<ShortLinkCreateRespDTO> result = new ArrayList<>();
        List<String> errorUrls = new ArrayList<>();
        for (int i = 0; i < originUrls.size(); i++) {
            ShortLinkCreateReqDTO shortLinkCreateReqDTO = BeanUtil.toBean(requestParam, ShortLinkCreateReqDTO.class);
            shortLinkCreateReqDTO.setOriginUrl(originUrls.get(i));
            shortLinkCreateReqDTO.setDescribe(describes.get(i));
            try {
                ShortLinkCreateRespDTO shortLink = createShortLink(shortLinkCreateReqDTO);
                ShortLinkCreateRespDTO linkBaseInfoRespDTO = ShortLinkCreateRespDTO.builder()
                        .fullShortUrl(shortLink.getFullShortUrl())
                        .originUrl(shortLink.getOriginUrl())
                        .gid(shortLink.getGid())
                        .build();
                result.add(linkBaseInfoRespDTO);
            } catch (Throwable ex) {
                errorUrls.add(originUrls.get(i));
            }
        }
        return ShortLinkBatchCreateRespDTO.builder()
                .total(result.size())
                .eachLinkInfos(result)
                .errorUrls(errorUrls)
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
                .favicon(Objects.equals(requestParam.getOriginUrl(), hasShortLinkDO.getOriginUrl()) ? hasShortLinkDO.getFavicon() : getFavicon(requestParam.getOriginUrl()))
                .createdType(hasShortLinkDO.getCreatedType())
                .gid(requestParam.getGid())
                .originUrl(requestParam.getOriginUrl())
                .describe(requestParam.getDescribe())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .build();
        baseMapper.update(shortLinkDO, updateWrapper);
    }

    private String generateSuffix(String originUrl) {
        int customGenerateCount = 0;
        String shortUri = HashGenerator.hashToBase62(originUrl);
        if (shortUri.length() == 5) {
            shortUri += 'a';
        }
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
            shortUri = HashGenerator.hashToBase62(originUrl + UUID.randomUUID());
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

    @SneakyThrows
    private void shortLinkStats(String fullShortUrl, ServletRequest request, ServletResponse response) {
        try {
            String remoteAddr = LinkUtil.getActualIp(((HttpServletRequest) request));
            String os = LinkUtil.getOs(((HttpServletRequest) request));
            String browser = LinkUtil.getBrowser(((HttpServletRequest) request));
            String device = LinkUtil.getDevice(((HttpServletRequest) request));
            String network = LinkUtil.getNetwork(((HttpServletRequest) request));
            boolean isNewUv = checkNewUv(fullShortUrl, request, response);
            boolean isNewUip = checkNewUip(fullShortUrl, request);
            int hour = DateUtil.hour(new Date(), true);
            Week week = DateUtil.dayOfWeekEnum(new Date());
            int weekValue = week.getIso8601Value();
            Date currentDate = new Date();

            LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
                    .pv(1)
                    .uv(isNewUv ? 1 : 0)
                    .uip(isNewUip ? 1 : 0)
                    .hour(hour)
                    .weekday(weekValue)
                    .fullShortUrl(fullShortUrl)
                    .date(currentDate)
                    .build();
            linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);

            Map<String, Object> localeParamMap = new HashMap<>();
            localeParamMap.put("key", statsLocaleAmapKey);
            localeParamMap.put("ip", remoteAddr);
            String localeResultStr = HttpUtil.get(AMAP_REMOTE_URL, localeParamMap);
            JSONObject localeResultObj = JSON.parseObject(localeResultStr);
            String infoCode = localeResultObj.getString("infocode");

            String actualProvince = "未知";
            String actualCity = "未知";
            if (StrUtil.isNotBlank(infoCode) && StrUtil.equals(infoCode, "10000")) {
                String province = localeResultObj.getString("province");
                boolean unknownFlag = StrUtil.equals(province, "[]");
                LinkLocaleStatsDO linkLocaleStatsDO = LinkLocaleStatsDO.builder()
                        .province(actualProvince = unknownFlag ? actualProvince : province)
                        .city(actualCity = unknownFlag ? actualCity : localeResultObj.getString("city"))
                        .adcode(unknownFlag ? "未知" : localeResultObj.getString("adcode"))
                        .cnt(1)
                        .fullShortUrl(fullShortUrl)
                        .country("中国")
                        .date(currentDate)
                        .build();
                linkLocaleStatsMapper.shortLinkLocaleState(linkLocaleStatsDO);
            }

            LinkOsStatsDO linkOsStatsDO = LinkOsStatsDO.builder()
                    .os(os)
                    .cnt(1)
                    .fullShortUrl(fullShortUrl)
                    .date(currentDate)
                    .build();
            linkOsStatsMapper.shortLinkOsState(linkOsStatsDO);

            LinkBrowserStatsDO linkBrowserStatsDO = LinkBrowserStatsDO.builder()
                    .browser(browser)
                    .cnt(1)
                    .fullShortUrl(fullShortUrl)
                    .date(currentDate)
                    .build();
            linkBrowserStatsMapper.shortLinkBrowserState(linkBrowserStatsDO);

            LinkDeviceStatsDO linkDeviceStatsDO = LinkDeviceStatsDO.builder()
                    .device(device)
                    .cnt(1)
                    .fullShortUrl(fullShortUrl)
                    .date(currentDate)
                    .build();
            linkDeviceStatsMapper.shortLinkDeviceState(linkDeviceStatsDO);

            LinkNetworkStatsDO linkNetworkStatsDO = LinkNetworkStatsDO.builder()
                    .network(network)
                    .cnt(1)
                    .fullShortUrl(fullShortUrl)
                    .date(currentDate)
                    .build();
            linkNetworkStatsMapper.shortLinkNetworkState(linkNetworkStatsDO);

            LinkAccessLogsDO linkAccessLogsDO = LinkAccessLogsDO.builder()
                    .user(UserContext.getUsername())
                    .ip(remoteAddr)
                    .browser(browser)
                    .os(os)
                    .network(network)
                    .device(device)
                    .locale(StrUtil.join("-", "中国", actualProvince, actualCity))
                    .fullShortUrl(fullShortUrl)
                    .build();
            linkAccessLogsMapper.insert(linkAccessLogsDO);

            shortLinkMapper.incrementStats(fullShortUrl, 1, isNewUv ? 1 : 0, isNewUip ? 1 : 0);

            LinkStatsTodayDO linkStatsTodayDO = LinkStatsTodayDO.builder()
                    .todayPv(1)
                    .todayUv(isNewUv ? 1 : 0)
                    .todayUip(isNewUip ? 1 : 0)
                    .fullShortUrl(fullShortUrl)
                    .date(currentDate)
                    .build();
            linkStatsTodayMapper.shortLinkTodayState(linkStatsTodayDO);
        } catch (Throwable ex) {
            // throw new ServiceException(SHORT_LINK_STATS_RECORD_ERROR);
            throw ex;
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
