package com.shop.service;

import com.shop.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileService {

    @Autowired
    private FileMapper fileMapper;

    private String savePath;

    public FileService(@Value("${file.dir-root}") String saveRootPath,
                       @Value("${file.dir-sub}") String saveSubPath) {
        createSaveFolder(saveRootPath);
        createSaveFolder(saveSubPath);
        savePath = saveRootPath + saveSubPath;
    }

    public void saveFile(MultipartFile[] files, String saveFolder, int id) {
        String path = savePath + File.separator + saveFolder;
        createSaveFolder(path);
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();

            Map map = new HashMap<>();
            map.put("fileName", fileName);
            map.put("path", path);
            map.put("productId", id);

            fileMapper.insertFile(map);

            try {
                file.transferTo(Paths.get(path + File.separator + map.get("id")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createSaveFolder(String path) {
        File saveFolder = new File(path);
        if (!saveFolder.exists()) {
            saveFolder.mkdir();
        }
    }

    public ResponseEntity<Resource> getImg(int id) {
        String path = fileMapper.getFilePath(id);
        path = path + File.separator + id;

        Path fileRoot = Paths.get(path);


        UrlResource urlResource= null;
        try {
            urlResource = new UrlResource(fileRoot.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + URLEncoder.encode(String.valueOf(id)) + "\"").body(urlResource);
    }
}
