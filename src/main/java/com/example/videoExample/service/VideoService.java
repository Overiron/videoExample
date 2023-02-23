package com.example.videoExample.service;

import com.example.videoExample.domain.Video;
import com.example.videoExample.dto.VideoResponse;
import com.example.videoExample.repository.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Transactional(readOnly = true)
@Slf4j
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;

    /**
     * upload file의 저장 경로 return
     * @return 저장 경로
     */
    public String getHome() {
        return "src\\main\\resources\\static\\ffmpeg";
    }

    /**
     * upload file에 대한 entity 생성 및 convert 위한 param setting
     * @param multipartFile
     * @param title
     * @param url
     * @return upload file info
     * @throws IOException
     */
    @Transactional
    public List<Object> uploadFile(MultipartFile multipartFile, String title, String url) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        String originalUrl = url + "/" +multipartFile.getOriginalFilename();
        String convertUrl = originalUrl.substring(0, originalUrl.length() - 4);
        Long fileSize = multipartFile.getSize();

        int totalSize = Long.valueOf(Optional.ofNullable(multipartFile.getSize()).orElse(0L)).intValue();

        if(totalSize > 104857600) {
            log.info("file size too big");
            throw new IOException();
        }

        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        if(!extension.equals("mp4")) {
            log.info("not mp4");
            throw new IOException();
        }

        String originalId = UUID.randomUUID().toString();
        List<Object> convertInfo = new ArrayList<>();

        String dirPath = new StringBuilder(getHome()).append(File.separator)
                .append(LocalDate.now()).toString();

        String fileName = String.format( "%s.%s", originalId
                , FilenameUtils.getExtension(multipartFile.getOriginalFilename()));

        File dir = new File(dirPath);
        if(!dir.exists()) {
            dir.mkdir();
        }

        String filePath = new StringBuilder(dirPath).append(File.separator).append(fileName).toString();
        Path path = Paths.get(filePath);
        Path absDirPath = Paths.get(dirPath);
        filePath = absDirPath.toString() + "\\" + originalId;

        //Files.write(path, multipartFile.getBytes());
        multipartFile.transferTo(path);

        Video video = Video.createVideo(originalId, title, dirPath, fileSize, originalUrl);

        videoRepository.upload(video);
        log.info("upload video id ===== "+String.valueOf(video.getId()));
        convertInfo.add(path);
        convertInfo.add(absDirPath.toString());
        convertInfo.add(originalId);
        convertInfo.add(video.getId());
        convertInfo.add(multipartFile.getSize());
        convertInfo.add(convertUrl);

        return convertInfo;
    }

    /**
     * 입력한 id에 대한 info를 조회
     * @param id
     * @return Video enmtity의 response
     */
    public VideoResponse findVideo(Long id) {
        return new VideoResponse(videoRepository.findById(id));
    }

    /**
     * progress 조회를 위해 video entity return
     * @param videoId
     * @return video entity
     */
    public Video getMeta(Long videoId) {
        return videoRepository.findById(videoId);
    }
}
