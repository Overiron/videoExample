package com.example.videoExample.domain;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "videos")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Video {
    @Id
    @Column(name = "video_id")
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "before_convert_id")
    private String beforeConvertId;

    @Column(name = "width")
    private int width;

    @Column(name = "height")
    private int height;

    @Column(name = "path")
    private String path;

    @Column(name = "url")
    private String url;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    //==Create Method==//
    public static Video createVideo(String originalId, String title, String path, Long fileSize) {
        Video video = new Video();

        video.setId(originalId);
        video.setTitle(title);
        video.setPath(path);
        video.setFileSize(fileSize);
        video.setCreatedDate(LocalDateTime.now());

        return video;
    }
    public static Video createVideo(String id, String title
            , Long fileSize, int width, int height
            , String path, String url, String originalId) {
        Video video = new Video();

        video.setId(id);
        video.setTitle(title);
        video.setFileSize(fileSize);
        video.setWidth(width);
        video.setHeight(height);
        video.setPath(path);
        video.setUrl(url);
        video.setBeforeConvertId(originalId);

        video.setCreatedDate(LocalDateTime.now());

        return video;
    }

    public static Video createVideo(MultipartFile multipartFile) {
        Video video = new Video();

        video.setTitle(Paths.get(multipartFile.getOriginalFilename()).getFileName().toString());

        video.setCreatedDate(LocalDateTime.now());

        return video;
    }

    //==business logic==/
}
