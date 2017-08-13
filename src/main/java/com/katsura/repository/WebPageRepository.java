package com.katsura.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.katsura.model.WebPage;
import com.katsura.model.WebPage.Status;

public interface WebPageRepository extends JpaRepository<WebPage, String> {
    
    WebPage findTopByStatus(Status status);
    
    @Modifying
    @Transactional
    @Query("update WebPage w set w.status = ?1")
    void resetStatus(Status status);
    
}
