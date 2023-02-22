package com.example.videoExample.repository;

import com.example.videoExample.domain.Video;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class VideoRepository {
    private final EntityManager em;

    public VideoRepository(EntityManager em)     {
        this.em = em;
    }

    public void upload(Video video) {
        em.persist(video);
    }

    public Video findById(String id) {
        return em.find(Video.class, id);
    }

    public void convert(Video video) {
        em.createQuery("update Video as v "
                + "set v.original_width = :width, v.original_height = :height "
                + "where v.id = :videoId")
//                .setParameter("beforeConvertId", video.getBeforeConvertId())
                .setParameter("width", video.getOriginal().getWidth())
                .setParameter("height", video.getOriginal().getHeight())
                .setParameter("videoId", video.getId())
                .executeUpdate();
    }

}
