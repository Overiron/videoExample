package com.example.videoExample.controller;

import com.example.videoExample.dto.VideoResponse;
import com.example.videoExample.service.VideoService;
import com.example.videoExample.service.VideoUtilsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("api/video")
@RequiredArgsConstructor
@Slf4j
public class VideoApiController {
    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoUtilsService videoUtilsService;

    private List<Object> file;

    @PostMapping("/upload")
    public HashMap<String, Object> uploadConvert(@RequestParam("uploadVideo") MultipartFile files
            , @RequestParam("title") String title
            , @RequestParam("url") String url) {
        HashMap<String, Object> map = new HashMap<String, Object>();

        try {
            file = videoService.uploadFile(files, title, url);
        } catch (IOException ie) {
            map.put("result", "fail");
            map.put("status", 400);

            return map;
        }

        final CompletableFuture<String> convertResult = videoUtilsService.convertVideo(file);
        convertResult.thenAccept(
                result -> {
                    if("fail".equals(result)) {
                        log.info("when fail=============");
                        map.put("result", "fail");
                        map.put("status", 400);
                        return ;
                    }
                }
        );

        map.put("result", "success");
        map.put("status", 200);

        return map;
    }


    @GetMapping("/progress/{id}")
    @ResponseBody
    public HashMap<String, Object> showProgress(@PathVariable("id") Long videoId) {
        double duration = videoUtilsService.getProgress(videoService.getMeta(videoId));

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("id", videoId);
        map.put("progress", duration);

        return map;
    }

    @GetMapping("/search/{id}")
    @ResponseBody
    public ResponseEntity<VideoResponse> search(@PathVariable("id") Long videoId) {
        return ResponseEntity.status(HttpStatus.OK).body(videoService.findVideo(videoId));
    }
}
