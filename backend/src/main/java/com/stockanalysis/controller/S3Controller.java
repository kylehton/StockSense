package com.stockanalysis.controller;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockanalysis.service.S3Service;

import jakarta.servlet.http.HttpSession;

@RestController
public class S3Controller {
    
    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    // no controller upload function because upload is automatically handled after
    // scraping for easier passthrough of data parameters to S3 bucket

    @RequestMapping("/s3/retrieve")
    public String[][] retrieveFromS3(@RequestParam String id, HttpSession session) {
        return s3Service.readNewsObjectContent(id);
    }

}
