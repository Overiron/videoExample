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

    public Video findById(Long id) {
        return em.find(Video.class, id);
    }

    public void videoProgress(Long id, double progress) {
        em.createQuery("update Video as v "
                + "set v.progress = :progress "
                + "where v.id = :id")
                .setParameter("id", id)
                .setParameter("progress", progress)
                .executeUpdate();
    }

}
