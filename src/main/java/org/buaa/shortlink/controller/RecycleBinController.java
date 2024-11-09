package org.buaa.shortlink.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.buaa.shortlink.common.convention.result.Result;
import org.buaa.shortlink.common.convention.result.Results;
import org.buaa.shortlink.dto.req.RecycleBinRecoverReqDTO;
import org.buaa.shortlink.dto.req.RecycleBinRemoveReqDTO;
import org.buaa.shortlink.dto.req.RecycleBinSaveReqDTO;
import org.buaa.shortlink.dto.resp.ShortLinkPageRespDTO;
import org.buaa.shortlink.service.RecycleBinService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回收站管理控制层
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    private final RecycleBinService recycleBinService;

    /**
     * 移至回收站
     */
    @PostMapping("/api/short-link/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam) {
        recycleBinService.saveRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 恢复短链接
     */
    @PostMapping("/api/short-link/recycle-bin/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam) {
        recycleBinService.recoverRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 移除短链接
     */
    @PostMapping("/api/short-link/recycle-bin/remove")
    public Result<Void> removeRecycleBin(@RequestBody RecycleBinRemoveReqDTO requestParam) {
        recycleBinService.removeRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 分页查询回收站短链接
     */
    @GetMapping("/api/short-link/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink() {
        return Results.success(recycleBinService.pageShortLink());
    }

}
