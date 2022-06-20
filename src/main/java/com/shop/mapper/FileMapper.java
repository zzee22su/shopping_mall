package com.shop.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface FileMapper {
    int insertFile(Map map);
    String getFilePath(int id);

    @Delete("DELETE FROM study_file WHERE product_id = #{productId}")
    int deleteFile(Long productId);

    @Select("SELECT id FROM study_file WHERE product_id = #{productId}")
    List<Long> getProductFileId(Long productId);
}
