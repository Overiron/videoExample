package com.example.videoExample.dto;

import com.example.videoExample.domain.Video;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class VideoResponse {
    private Long id;
    private String title;
    private String path;
    private String originalId;
    private Long originalSize;
    private int originalWidth;
    private int originalHeight;
    private String originalUrl;
    private String convertId;
    private Long convertSize;
    private int convertWidth;
    private int convertHeight;
    private String convertUrl;
    private LocalDateTime createdDate;

    public VideoResponse(Video video) {
        this.id = video.getId();
        this.title = video.getTitle();
        this.path = video.getPath();

        this.originalId = video.getOriginal().getVideoId();
        this.originalSize = video.getOriginal().getFileSize();
        this.originalWidth = video.getOriginal().getWidth();
        this.originalHeight = video.getOriginal().getHeight();
        this.originalUrl = video.getOriginal().getUrl();

        this.convertId = video.getOriginal().getVideoId();
        this.convertSize = video.getOriginal().getFileSize();
        this.convertWidth = video.getOriginal().getWidth();
        this.convertHeight = video.getOriginal().getHeight();
        this.convertUrl = video.getConverted().getUrl();

        this.createdDate = video.getCreatedDate();
    }

}
