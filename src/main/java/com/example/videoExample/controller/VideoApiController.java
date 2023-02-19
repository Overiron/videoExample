package com.example.videoExample.controller;

import com.example.videoExample.domain.Video;
import com.example.videoExample.service.VideoService;
import com.example.videoExample.service.VideoUtilsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/video")
@RequiredArgsConstructor
@Slf4j
public class VideoApiController {
    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoUtilsService videoUtilsService;

    @PostMapping("/upload")
    public String save(@RequestParam("uploadVideo") List<MultipartFile> files
                      , @RequestParam("title") String title) throws Exception {
        videoUtilsService.uploadToConvert(videoService.createFile(files, title));

        return "";
    }

    @PostMapping("/search")
    @ResponseBody
    public Video search(@RequestParam("videoId") String videoId) {
        //videoService.getMeta(videoId);
        return videoService.getMeta(videoId);
    }
}
