package com.example.videoExample.service;

import com.example.videoExample.domain.Video;
import com.example.videoExample.domain.VideoInfo;
import com.example.videoExample.repository.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional(readOnly = true)
@Slf4j
public class VideoUtilsService {
    @Autowired
    private VideoRepository videoRepository;
    private final static String FFMPEG_PATH = "src/main/resources/static/ffmpeg/ffmpeg";
    private final static String FFPROBE_PATH = "src/main/resources/static/ffmpeg/ffprobe";
    private FFmpeg ffmpeg = new FFmpeg(FFMPEG_PATH);
    private FFprobe ffprobe = new FFprobe(FFPROBE_PATH);

    FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

    FFmpegJob ffmpegJob;

    FFmpegProbeResult ffmpegProbeResult;

    public VideoUtilsService() throws IOException {}

//    @Transactional
//    public CompletableFuture<String> uploadToConvert(List<Object> files) {
//        String result = "fail";
//
//        return CompletableFuture.completedFuture(result);
//    }

    @Transactional
    @Async
    public CompletableFuture<String> convertVideo(List<Object> file) {
        String result = "success";

        Long startTime = System.currentTimeMillis();
        String inputFileName = file.get(0).toString();
        String outputPath = file.get(1).toString();
        String originalId = file.get(2).toString();

        getMeta(originalId);

        Video input = videoRepository.findById(originalId);
        String title = input.getTitle();
        Long fileSize = Long.parseLong(file.get(3).toString());
        String url = "";
        String convertId = UUID.randomUUID().toString();
//        int originalWidth = input.getWidth();
        int originalWidth = input.getOriginal().getWidth();
        int originalHeight = input.getOriginal().getHeight();

        int rate = originalWidth / 360;
        int height = originalHeight / rate;
        log.info("height============"+height);
        int width = 360;

        try {
            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(inputFileName)
                    .overrideOutputFiles(false)
                    .addOutput(outputPath + "\\" + convertId + ".mp4")
                    .setFormat("mp4")
                    .setVideoResolution(360, height)
                    .done();
            Thread.sleep(3000);
            //executor.createJob(builder).run();
            ffmpegJob = executor.createJob(builder);
            ffmpegJob.run();



        } catch (Exception e) {
            log.info("convert fail");
            result = "fail";
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Long completeTime = System.currentTimeMillis();
        Long diffTime = (completeTime - startTime) / 1000;

        input.setConverted(new VideoInfo(convertId, fileSize, width, height, url));

//        Video video = Video.createConvertVideo(convertId, title, fileSize, width, height, outputPath, url, originalId);
//        Video video = Video.createConvertVideo(convertId, fileSize, width, height,  url);

//        videoRepository.upload(video);
        videoRepository.upload(input);

        makeThumbnail(originalId);

        return CompletableFuture.completedFuture(result);
    }

    @Transactional
    public void getMeta(String videoId) {
        Video video = videoRepository.findById(videoId);

        try {
            ffmpegProbeResult = ffprobe.probe(video.getPath()+"\\"+videoId+".mp4");
//            video.setWidth(fFmpegProbeResult.getStreams().get(0).width);
//            video.setHeight(fFmpegProbeResult.getStreams().get(0).height);

            video.setOriginal((new VideoInfo(ffmpegProbeResult.getStreams().get(0).width
                    , ffmpegProbeResult.getStreams().get(0).height)));

            log.info("resolution width ===== " + ffmpegProbeResult.getStreams().get(0).width);
            log.info("resolution height ===== " + ffmpegProbeResult.getStreams().get(0).height);

        } catch(IOException ie) {
            ie.printStackTrace();
        }

//        videoRepository.convert(video);
        videoRepository.upload(video);
    }

    @Transactional
    public void makeThumbnail(String videoId) {
        Video video = videoRepository.findById(videoId);
        String outputFile = video.getPath()+"\\"+videoId+"-th.jpg";
        String inputFile = video.getPath()+"\\"+videoId+".mp4";
        int width = video.getOriginal().getWidth();
        int height = video.getOriginal().getHeight();

        FFmpegBuilder builder = new FFmpegBuilder()
                .overrideOutputFiles(true)
                .setInput(inputFile)
                .addOutput(outputFile)
                .setFrames(1)
                //.setFilter("select='gte(n\\,10)',scale=200:-1")
                .done();
        log.info(builder.toString());
        FFmpegJob ffmpegJobThumb = executor.createJob(builder);
        ffmpegJobThumb.run();
        log.info("thumbnail state ===== "+ffmpegJobThumb.getState().toString());
    }

    public String getProgress(String videoId) {
        String state = ffmpegJob.getState().toString();

        double tmp = ffmpegProbeResult.getStreams().get(0).duration;

        return state;
    }
}
