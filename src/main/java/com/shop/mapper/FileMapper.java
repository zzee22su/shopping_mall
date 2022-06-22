package com.shop.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface FileMapper {
    int insertFile(Map map);
    String getFilePath(Long id);

    @Delete("DELETE FROM study_file WHERE product_id = #{productId}")
    int deleteFile(Long productId);

    @Select("SELECT id FROM study_file WHERE product_id = #{productId}")
    List<Long> getProductFileId(Long productId);

    @Update("UPDATE study_file SET product_id=#{productId} WHERE id=#{id}")
    int updateProductFileId(Long productId,Long id);
}
