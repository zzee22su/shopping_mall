package com.shop.mapper;

import com.shop.domain.model.ProductListModel;
import com.shop.domain.model.ProductModel;
import com.shop.domain.request.ProductInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ProductMapper {
    int insertProduct(ProductInfo productInfo);

    int insertOption(List<ProductInfo.ProductionOption> productionOptions);

    int insertOptionType(List<ProductInfo.ProductionOption> productionOptions);

    ProductModel getProductModel(int productId);

    @Select("SELECT COUNT(*) FROM study_product")
    int getProductCount();

    List<ProductListModel> getProductList(Map map);


}