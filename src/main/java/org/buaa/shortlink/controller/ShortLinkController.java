package org.buaa.shortlink.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import org.buaa.shortlink.common.convention.result.Result;
import org.buaa.shortlink.common.convention.result.Results;
import org.buaa.shortlink.dto.req.ShortLinkCreateReqDTO;
import org.buaa.shortlink.dto.req.ShortLinkPageReqDTO;
import org.buaa.shortlink.dto.req.ShortLinkUpdateReqDTO;
import org.buaa.shortlink.dto.resp.ShortLinkCreateRespDTO;
import org.buaa.shortlink.dto.resp.ShortLinkPageRespDTO;
import org.buaa.shortlink.service.ShortLinkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接控制层
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return Results.success(shortLinkService.createShortLink(requestParam));
    }

    /**
     * 分页查询短链接
     */
    @GetMapping("/api/short-link/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return Results.success(shortLinkService.pageShortLink(requestParam));
    }

    /**
     * 修改短链接
     */
    @PostMapping("/api/short-link/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        shortLinkService.updateShortLink(requestParam);
        return Results.success();
    }

    /**
     * 短链接跳转
     */
    @GetMapping("/{short-uri}")
    public void restoreUrl(@PathVariable("short-uri") String shortUri, ServletRequest request, ServletResponse response) {
        shortLinkService.restoreUrl(shortUri, request, response);
    }

}
