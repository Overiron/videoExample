package com.example.videoExample.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "videos")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Video {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "path")
    private String path;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "videoId", column = @Column(name = "original_id"))
            , @AttributeOverride(name = "fileSize", column = @Column(name = "original_size"))
            , @AttributeOverride(name = "width", column = @Column(name = "original_width"))
            , @AttributeOverride(name = "height", column = @Column(name = "original_height"))
            , @AttributeOverride(name = "url", column = @Column(name = "original_url"))
    })
    private VideoInfo original;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "videoId", column = @Column(name = "convert_id"))
            , @AttributeOverride(name = "fileSize", column = @Column(name = "convert_size"))
            , @AttributeOverride(name = "width", column = @Column(name = "convert_width"))
            , @AttributeOverride(name = "height", column = @Column(name = "convert_height"))
            , @AttributeOverride(name = "url", column = @Column(name = "convert_url"))
    })
    private VideoInfo converted;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    //==Create Method==//
    public static Video createVideo(String originalId, String title, String path, Long fileSize, String url) {
        Video video = new Video();

        video.setTitle(title);
        video.setPath(path);
        video.setOriginal(new VideoInfo(originalId, fileSize, url));
        video.setCreatedDate(LocalDateTime.now());

        return video;
    }
//    publ static Video createVideo(Long id, String title
////            , Long fileSize, int width, int height
////            , String path, String url, String originalId) {
////        Video video = new Video();
////
////        video.setId(id);
////        video.setTitle(title);
////        video.setPath(path);
////
////        video.setOriginal(new VideoInfo(fileSize, width, height, url));
////
////        video.setCreatedDate(LocalDateTime.now());
////
////        return video;
////    }
////
////    public static Video createConvertVideo(String id, Long fileSize, int width, int height, String url) {
////        Video video = new Video();
////
////        video.setConverted(new VideoInfo(id, fileSize, width, height, url));
////
////        return video;
////    }ic
}
