package com.example.videoExample.controller;

import com.example.videoExample.service.VideoService;
import com.example.videoExample.service.VideoUtilsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoUtilsService videoUtilsService;

    @GetMapping("/upload/fileUpload")
    public String uploadForm() {
        return "upload/videoUpload";
    }

    @GetMapping("/upload/fileSearch")
    public String searchForm() {
        return "upload/videoSearch";
    }
}
