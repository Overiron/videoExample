package com.example.videoExample.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class VideoInfo {

    private String videoId;
    private Long fileSize;
    private int width;
    private int height;
    private String url;

    protected VideoInfo() {
    }

    public VideoInfo(String videoId, Long fileSize, String url) {
        this.videoId = videoId;
        this.fileSize = fileSize;
        this.url = url;
    }

    public VideoInfo(int width, int height) {
        this.width = width;
        this.height = height;
    }
    public VideoInfo(Long fileSize, int width, int height, String url) {
        this.fileSize = fileSize;
        this.width = width;
        this.height = height;
        this.url = url;
    }

    public VideoInfo(String videoId, Long fileSize, int width, int height, String url) {
        this.videoId = videoId;
        this.fileSize = fileSize;
        this.width = width;
        this.height = height;
        this.url = url;
    }
}
