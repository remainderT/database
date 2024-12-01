package org.buaa.shortlink.controller;

import lombok.RequiredArgsConstructor;
import org.buaa.shortlink.common.convention.result.Result;
import org.buaa.shortlink.common.convention.result.Results;
import org.buaa.shortlink.service.ImageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片管理控制层
 */
@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    /**
     * oss上传图片
     */
    @PostMapping("/api/short-link/image")
    public Result<String> ossUploadImage(@RequestParam("file") MultipartFile file) {
        return Results.success(imageService.uploadImage(file));
    }

}
