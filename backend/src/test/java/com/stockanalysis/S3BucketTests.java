package com.stockanalysis;
import com.stockanalysis.service.S3Service;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class S3BucketTests {
    
    private S3Service s3Service;

    public void TestS3Connection() 
    {
        try
        {
            s3Service.checkS3Connection();
            System.out.println("S3 connection successful");
        }
        catch(Exception e){
            System.out.println("S3 connection failed: " + e.getMessage());
        }
    }

}
