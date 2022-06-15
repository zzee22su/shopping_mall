package com.shop.controller;

import com.shop.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}