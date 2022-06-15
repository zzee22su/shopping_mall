package com.shop.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Mapper
@Repository
public interface FileMapper {
    int insertFile(Map map);
    String getFilePath(int id);
}
