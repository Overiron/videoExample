package com.example.videoExample.controller;

import com.example.videoExample.domain.Video;
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
import java.util.concurrent.ExecutionException;

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
    public HashMap<String, Object> save(@RequestParam("uploadVideo") MultipartFile files
            , @RequestParam("title") String title) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        try {
            file = videoService.uploadFile(files, title);
        } catch (IOException ie) {
            map.put("result", "fail");
            map.put("status", 400);

            return map;
        }
        final CompletableFuture<String> convertResult =
                videoUtilsService.convertVideo(file);

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

        try {
            convertResult.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        map.put("result", "success");
        map.put("status", 200);

        return map;
    }

    public String convert(List<Object> file) throws ExecutionException, InterruptedException {
        String returnVal = "fail";

        videoUtilsService.convertVideo(file);

        final CompletableFuture<String> convertResult = videoUtilsService.convertVideo(file);
        convertResult.thenApply(
                result -> {
                    if("fail".equals(result)) {
                        return "fail";
                    }
                    return "success";
                }
        );

        returnVal  = convertResult.get();

        return returnVal;
    }

    @PostMapping("/progress")
    public void showProgress(@RequestParam("videoId") String videoId) {

    }

    @PostMapping("/search")
    @ResponseBody
    public ResponseEntity<Video> search(@RequestParam("videoId") String videoId) {
        return ResponseEntity.status(HttpStatus.OK).body(videoService.getMeta(videoId));
    }
}
