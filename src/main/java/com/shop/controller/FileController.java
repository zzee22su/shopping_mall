package com.shop.controller;

import com.shop.response.ResponseData;
import com.shop.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping(value="/img/{id}")
    public ResponseEntity<Resource> getImg(@PathVariable int id){
        return fileService.getImg(id);
    }

    @PostMapping("/img")
    public ResponseEntity<ResponseData> imageUpload(HttpServletRequest request, @RequestParam MultipartFile file, @RequestParam int productId){
        return fileService.imgUpload(file,productId);
    }
}