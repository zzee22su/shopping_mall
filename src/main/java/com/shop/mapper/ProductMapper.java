package com.shop.mapper;

import com.shop.domain.model.ProductListModel;
import com.shop.domain.model.ProductModel;
import com.shop.domain.request.ProductInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ProductMapper {
    int insertProduct(ProductInfo productInfo);

    int updateProduct(ProductInfo productInfo);

    int insertOption(List<ProductInfo.ProductionOption> productionOptions);

    int insertOptionType(List<ProductInfo.ProductionOption> productionOptions);

    ProductModel getProductModel(Long productId);

    int getProductCount(String category);

    List<ProductListModel> getProductList(Map map);

    //List<Long> getOptionId(int productId);

    @Select("SELECT id FROM study_option WHERE product_id = #{productId}")
    List<Long> getOptionId(Long productId);

    @Delete("DELETE FROM study_option_type WHERE option_id = #{id}")
    void deleteOptionType(Long id);

    @Delete("DELETE FROM study_option WHERE product_id = #{id}")
    void deleteOption(Long id);

    @Delete("DELETE FROM study_product WHERE id = #{id}")
    void deleteProduct(Long id);
}