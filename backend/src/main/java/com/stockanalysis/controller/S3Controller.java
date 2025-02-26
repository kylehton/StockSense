package com.stockanalysis.controller;

import java.util.Date;

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
    public String retrieveFromS3(@RequestParam String id, HttpSession session) {
        try{
        System.out.println("all session attributes: " + session.getAttributeNames());
        System.out.println("Current Session ID: " + session.getId());
        System.out.println("Session Creation Time: " + new Date(session.getCreationTime()));
        System.out.println("Session Last Accessed Time: " + new Date(session.getLastAccessedTime()));
        System.out.println("Is Session New: " + session.isNew());
        return s3Service.readObjectContent(id);
        }
        catch(Exception e){
          return "Error retrieving from S3: " + e.getMessage();
        }
    }

}
