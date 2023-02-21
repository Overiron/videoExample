package com.example.videoExample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class VideoExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoExampleApplication.class, args);
	}

}
