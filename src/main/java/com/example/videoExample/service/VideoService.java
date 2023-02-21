package com.example.videoExample.service;

import com.example.videoExample.domain.Video;
import com.example.videoExample.repository.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
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

    public String getHome() {
        return "src\\main\\resources\\static\\ffmpeg";
        //return System.getProperty("user.home");
    }

    //service에서는 controller가 보낸 요청을 받고
    //db에 접근해야 될 부분은 repo로 domain에 관련된 business logic은 domin으로 보내주자

    @Transactional
    public List<Object> createFile(MultipartFile multipartFile, String title) throws IOException {
        List<Object> convertFile = new ArrayList<Object>();

        int totalSize = Long.valueOf(Optional.ofNullable(multipartFile.getSize()).orElse(0L)).intValue();

        if(totalSize > 104857600) {
            throw new IOException();
        } else {
        }

        return convertFile;
    }

    @Transactional
    public List<Object> uploadFile(MultipartFile multipartFile, String title) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
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

        convertInfo.add(path);
        log.info("path ===== "+path);
        convertInfo.add(absDirPath.toString());
        log.info("absDirPath ===== "+absDirPath.toString());
        convertInfo.add(originalId);
        log.info("originalId ===== "+originalId);
        convertInfo.add(multipartFile.getSize());
        log.info("getSize ===== "+multipartFile.getSize());

        log.info("convertInfo ===== "+convertInfo.get(0));
        log.info("convertInfo ===== "+convertInfo.get(1));
        log.info("convertInfo ===== "+convertInfo.get(2));
        log.info("convertInfo ===== "+convertInfo.get(3));

        //Files.write(path, multipartFile.getBytes());
        multipartFile.transferTo(path);

        Video video = Video.createVideo(originalId, title, dirPath, fileSize);
        log.info("Video Entity Id ==== "+video.getId());
        videoRepository.upload(video);

        return convertInfo;
    }

    public Video getMeta(String videoId) {
        //Video video = videoRepository.findById(videoId);

        return videoRepository.findById(videoId);
    }

    public byte[] getFile(String path) throws IOException {
        File file = new File(path);
        byte[] bytes = null;
        if(file.exists()) {
            bytes = FileUtils.readFileToByteArray(file);
        }

        return bytes;
    }
}
