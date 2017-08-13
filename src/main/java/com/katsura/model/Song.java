package com.katsura.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Song {
    
    @Id
    private String url;
    private String title;
    private Long commentCount;
    
    public Song() {
        super();
    }
    
    public Song(String url, String title, Long commentCount) {
        super();
        this.url = url;
        this.title = title;
        this.commentCount = commentCount;
    }
}
