package com.he180773.testreact.controller.client;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class ImageUploadController {

    @Autowired
    private Cloudinary cloudinary;

    @PostMapping
    public ResponseEntity<List<String>> upload(@RequestParam("files") MultipartFile[] files) throws IOException {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            urls.add((String) uploadResult.get("secure_url"));
        }
        return ResponseEntity.ok(urls);
    }
    @PostMapping(
            value = "/single",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> uploadSingle(@RequestParam("file") MultipartFile file) throws IOException {
        System.out.println(file.getOriginalFilename());
        String urls = "";

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            urls=(String) uploadResult.get("secure_url");

        return ResponseEntity.ok(urls);
    }
}

