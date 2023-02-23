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
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
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

    double outProgress = 0;

    public VideoUtilsService() throws IOException {}


    /**
     * upload file의 resolution을 변경 후 저장
     * @param file
     * @return async 상태값
     */
    @Transactional
    @Async
    public CompletableFuture<String> convertVideo(List<Object> file) {
        String result = "success";

        String inputFileName = file.get(0).toString();
        String outputPath = file.get(1).toString();
        String originalId = file.get(2).toString();
        Long videoId = (Long) file.get(3);

        String convertUrl = file.get(5).toString()+"_convert.mp4";

        changeOriginalInfo(videoId);

        Video input = videoRepository.findById(videoId);
        Long fileSize = Long.parseLong(file.get(4).toString());

        String convertId = UUID.randomUUID().toString();
        int originalWidth = input.getOriginal().getWidth();
        int originalHeight = input.getOriginal().getHeight();

        int rate = originalWidth / 360;
        int height = originalHeight / rate;
        int width = 360;

        try {
            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(inputFileName)
                    .overrideOutputFiles(false)
                    .addOutput(outputPath + "\\" + convertId + ".mp4")
                    .setFormat("mp4")
                    .setVideoResolution(360, height)
                    .done();
            //executor.createJob(builder).run();
            Thread.sleep(5000);
            ffmpegJob = executor.createJob(builder, new ProgressListener() {
                final double input_duration = ffmpegProbeResult.getFormat().duration;
                @Override
                public void progress(Progress progress) {
                    double duration = (progress.out_time_ns / input_duration) * 100;
                    outProgress = duration;
                }
            });
            ffmpegJob.run();

        } catch (Exception e) {
            log.info("convert fail");
            result = "fail";
        }
        input.setConverted(new VideoInfo(convertId, fileSize, width, height, convertUrl));

        videoRepository.upload(input);

        makeThumbnail(videoId);

        return CompletableFuture.completedFuture(result);
    }


    /**
     * Video entity의 original width와 height를 update
     * @param videoId
     */
    @Transactional
    public void changeOriginalInfo(Long videoId) {
        Video video = videoRepository.findById(videoId);

        try {
            ffmpegProbeResult = ffprobe.probe(video.getPath()+"\\"+video.getOriginal().getVideoId()+".mp4");

            video.setOriginal(new VideoInfo(video.getOriginal().getVideoId(), video.getOriginal().getFileSize()
                    , ffmpegProbeResult.getStreams().get(0).width, ffmpegProbeResult.getStreams().get(0).height
                    , video.getOriginal().getUrl()));

        } catch(IOException ie) {
            ie.printStackTrace();
        }

        videoRepository.upload(video);
    }

    /**
     * upload file의 thumbnail 생성
     * @param videoId
     */
    @Transactional
    public void makeThumbnail(Long videoId) {
        Video video = videoRepository.findById(videoId);
        String originalId = video.getOriginal().getVideoId();
        String outputFile = video.getPath()+"\\"+originalId+"-th.jpg";
        String inputFile = video.getPath()+"\\"+originalId+".mp4";
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

    /**
     * convert progress 조회
     * @param video
     * @return convert progress(percent)
     */
    public double getProgress(Video video) {
////        String state = ffmpegJob.getState().toString();
//        double inputDur = 0, outputDur = 0;
//
//        FFmpegProbeResult ffmpegProbeInput = null;
//        FFmpegProbeResult ffmpegProbeOutput = null;
//
//        try {
//            ffmpegProbeInput = ffprobe.probe(video.getPath()+"\\"+video.getOriginal().getVideoId()+".mp4");
//            ffmpegProbeOutput = ffprobe.probe(video.getPath()+"\\"+video.getConverted().getVideoId()+".mp4");
//
//
//            inputDur = ffmpegProbeInput.getStreams().get(0).duration;
//            outputDur = ffmpegProbeOutput.getStreams().get(0).duration;
//
//            log.info("inputDuration ======= "+inputDur);
//            log.info("outputDuration ======= "+outputDur);
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        double tmp = ffmpegProbeResult.getStreams().get(0).duration;

        return outProgress;
    }
}

