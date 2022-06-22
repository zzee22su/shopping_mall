package com.shop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.domain.request.ProductInfo;
import com.shop.response.ResponseData;
import com.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/product")
    public ResponseEntity<ResponseData> createProduct(@RequestParam("productInfo") String productInfoParam,
                                                      @RequestPart(required = false) MultipartFile[] files) throws JsonProcessingException {
        ProductInfo productInfo = new ObjectMapper().readValue(productInfoParam, ProductInfo.class);
        return productService.createProduct(productInfo,files);
    }

    @GetMapping(value="/product/{id}")
    public ResponseEntity<ResponseData> getProduct(@PathVariable Long id){
        return productService.getProduct(id);
    }

    @GetMapping("/product")
    public ResponseEntity<ResponseData> getProductList(HttpServletRequest request){
        return productService.getProductList(request);
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<ResponseData> deleteProduct(@PathVariable Long id){
        return productService.deleteProduct(id);
    }
}