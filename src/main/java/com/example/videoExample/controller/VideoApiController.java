package com.example.videoExample.controller;

import com.example.videoExample.domain.Video;
import com.example.videoExample.service.VideoService;
import com.example.videoExample.service.VideoUtilsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public HashMap<String, Object> save(@RequestParam("uploadVideo") MultipartFile files
                      , @RequestParam("title") String title) throws Exception {
        HashMap<String, Object> map = new HashMap<String, Object>();
        file = videoService.uploadFile(files, title);

//        final CompletableFuture<String> convertResult =
////                videoUtilsService.convertVideo(videoService.uploadFile(files, title));
//                videoUtilsService.convertVideo(file);
//        convertResult.thenAccept(
//                result -> {
//                    if("fail".equals(result)) {
//                        map.put("result", "fail");
//                        map.put("status", 400);
//                        return;
//                    }
//                }
//        );

        map.put("result", "success");
        map.put("status", 200);

        return map;
    }

    public boolean convert(List<Object> file) {
        boolean retVal = false;

        videoUtilsService.convertVideo(file);

        final CompletableFuture<String> convertResult = videoUtilsService.convertVideo(file);
        convertResult.thenAccept(
                result -> {
                    log.info("response result ", result);
                    log.info("videoUtilServicing ====================");
                    if("fail".equals(result)) {
                        log.info("fail to Converting ====================");
                        return;
                    }
                }
        );

        retVal = true;
        log.info("convert success ================");
        log.info("videoUtilService after call ====================");

        return retVal;
    }

    @PostMapping("/search")
    @ResponseBody
    public Video search(@RequestParam("videoId") String videoId) {
        return videoService.getMeta(videoId);
    }
}
