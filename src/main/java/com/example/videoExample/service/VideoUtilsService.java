package com.example.videoExample.service;

import com.example.videoExample.domain.Video;
import com.example.videoExample.repository.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

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

    FFmpegProbeResult fFmpegProbeResult;

    public VideoUtilsService() throws IOException {}

    @Transactional
    public void uploadToConvert(List<List<Object>> files) {
        for(List<Object> file : files) {
            convertVideo(file);
        }
    }
    @Transactional
    public void convertVideo(List<Object> file) {
        Long startTime = System.currentTimeMillis();
        String inputFileName = file.get(0).toString();
        String outputPath = file.get(1).toString();
        String originalId = file.get(2).toString();
        String title = videoRepository.findById(originalId).getTitle();
        Long fileSize = Long.parseLong(file.get(3).toString());
        String url = "";
        String convertId = UUID.randomUUID().toString();
        int height = 200;
        int width = 360;


        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(inputFileName)
                .overrideOutputFiles(false)
                .addOutput(outputPath + "\\" + convertId + ".mp4")
                .setFormat("mp4")
                .setVideoResolution(360, 200)
                .done();

        //executor.createJob(builder).run();
        ffmpegJob = executor.createJob(builder);
        ffmpegJob.run();
        log.info("convert state ===== "+ffmpegJob.getState().toString());

        Long completeTime = System.currentTimeMillis();
        Long diffTime = (completeTime - startTime) / 1000;

        Video video = Video.createVideo(convertId, title, fileSize, width, height, outputPath, url, originalId);

        videoRepository.upload(video);

        getMeta(originalId);

        makeThumbnail(originalId);
    }

    @Transactional
    public void getMeta(String videoId) {
        Video video = videoRepository.findById(videoId);

        try {
            fFmpegProbeResult = ffprobe.probe(video.getPath()+"\\"+videoId+".mp4");
            video.setWidth(fFmpegProbeResult.getStreams().get(0).width);
            video.setHeight(fFmpegProbeResult.getStreams().get(0).height);

            log.info("resolution width ===== " + fFmpegProbeResult.getStreams().get(0).width);
            log.info("resolution height ===== " + fFmpegProbeResult.getStreams().get(0).height);

        } catch(IOException ie) {
            ie.printStackTrace();
        }

        videoRepository.convert(video);
    }

    @Transactional
    public void makeThumbnail(String videoId) {
        log.info("thumbnail ===== " + videoId);

        Video video = videoRepository.findById(videoId);
        String outputFile = video.getPath()+"\\"+videoId+"-th.jpg";
        log.info("thumnail output file ===== " + outputFile);
        String inputFile = video.getPath()+"\\"+videoId+".mp4";
        log.info("thumnail input file ===== " + inputFile);
        int width = video.getWidth();
        int height = video.getHeight();

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

    public String getConvertState(String videoId) {
        String state = ffmpegJob.getState().toString();

        return state;
    }
}
