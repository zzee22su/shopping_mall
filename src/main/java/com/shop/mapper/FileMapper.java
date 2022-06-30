package com.shop.mapper;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface FileMapper {
    int insertFile(Map map);

    String getFilePath(Long id);

    @Select("SELECT file_group from study_product where id=#{productId}")
    Long getProductToFileGroupId(Long productId);

    @Delete("DELETE FROM study_file_group WHERE id=#{id}")
    int deleteFileGroup(Long id);

    @Delete("DELETE FROM study_file WHERE file_group = #{fileGroupId}")
    int deleteProductFiles(Long fileGroupId);

    @Delete("DELETE FROM study_file WHERE id = #{id}")
    int deleteFile(Long id);

    @Select("SELECT id FROM study_file WHERE file_group = #{fileGroupId}")
    List<Long> getProductFileId(Long fileGroupId);

    @Select("SELECT id FROM study_file WHERE product_id = #{productId} AND contentImg=1")
    List<Long> getContentImgId(Long productId);

    @Update("UPDATE study_file SET file_group=#{groupId} WHERE id=#{id}")
    int updateFileToGroupId(Long groupId,Long id);

    @Select("SELECT EXISTS( SELECT id FROM study_file WHERE product_id = #{productId} AND id = #{fileId}) AS success")
    boolean isExistsFileInProduct(Long productId, Long fileId);

    @Insert("INSERT INTO study_file_group (group_name) VALUES (#{groupName})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertFileGroup(Map map);
}