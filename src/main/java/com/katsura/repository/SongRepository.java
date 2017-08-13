package com.katsura.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.katsura.model.Song;

public interface SongRepository extends JpaRepository<Song, String> {
    
    List<Song> findByCommentCountGreaterThan(Long commentCount);
    
}
