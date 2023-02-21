package com.example.videoExample.controller;

import com.example.videoExample.domain.Video;
import com.example.videoExample.service.VideoService;
import com.example.videoExample.service.VideoUtilsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@EnableAsync
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
        // file = videoService.uploadFile(files, title);
        videoUtilsService.convertVideo(videoService.uploadFile(files, title));

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

    public String convert(List<Object> file) throws ExecutionException, InterruptedException {
        String returnVal = "fail";

        videoUtilsService.convertVideo(file);

        final CompletableFuture<String> convertResult = videoUtilsService.convertVideo(file);
        convertResult.thenApply(
                result -> {
                    log.info("response result ", result);
                    log.info("videoUtilServicing ====================");
                    if("fail".equals(result)) {
                        log.info("fail to Converting ====================");
                        return "fail";
                    }
                    return "success";
                }
        );

        returnVal  = convertResult.get();

        log.info("returnVal ================ " + returnVal);
        log.info("videoUtilService after call ====================");

        return returnVal;
    }


//    @PostMapping("/search")
//    @ResponseBody
//    public Video search(@RequestParam("videoId") String videoId) {
//        //videoService.getMeta(videoId);
//        return videoService.getMeta(videoId);
//    }

    @PostMapping("/search")
    @ResponseBody
    public ResponseEntity<Video> search(@RequestParam("videoId") String videoId) {
        //videoService.getMeta(videoId);
        return ResponseEntity.status(HttpStatus.OK).body(videoService.getMeta(videoId));
    }
}
