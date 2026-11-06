package com.power.controller.admin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.power.result.Result;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @Value("${power.upload-dir:./uploads}")
    private String uploadDir;

    /**
     * 上传图片
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("上传图片：{}", file);
        String originalFilename = file.getOriginalFilename();
        String suffix = ".jpg";
        if (originalFilename != null) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + suffix;
        String imgUrl = "/media/" + fileName;
        try {
            Path targetDirectory = Path.of(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(targetDirectory);
            file.transferTo(targetDirectory.resolve(fileName));
            return Result.success(imgUrl);
        } catch (IllegalStateException | IOException e) {
            log.error("上传图片失败：{}", e);
            return Result.error("上传图片失败");
        }
    }
}
