package org.buaa.shortlink.controller;

import lombok.RequiredArgsConstructor;
import org.buaa.shortlink.common.convention.result.Result;
import org.buaa.shortlink.common.convention.result.Results;
import org.buaa.shortlink.dto.req.RecycleBinSaveReqDTO;
import org.buaa.shortlink.service.RecycleBinService;
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

}
