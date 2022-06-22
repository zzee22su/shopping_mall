package com.shop.service;

import com.shop.domain.model.ProductListModel;
import com.shop.domain.model.ProductModel;
import com.shop.domain.request.ProductInfo;
import com.shop.mapper.FileMapper;
import com.shop.mapper.ProductMapper;
import com.shop.response.Response;
import com.shop.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shop.response.ErrorCode.INVALID_NO_INPUT_PAGE;

@Service
public class ProductService {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private FileService fileService;

    @Transactional(propagation = Propagation.REQUIRED, transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseEntity<ResponseData> createProduct(ProductInfo productInfo, MultipartFile[] files) {
        int value = productMapper.insertProduct(productInfo);

        productInfo.getProductionOptions().forEach(productionOption -> {
                    productionOption.setProductId(productInfo.getId());
                }
        );

        value = productMapper.insertOption(productInfo.getProductionOptions());

        productInfo.getProductionOptions().forEach(productionOption -> {
                    productionOption.getOptionValues().forEach(optionValue -> {
                        optionValue.setOptionTypeId(productionOption.getId());
                    });
                }
        );

        value = productMapper.insertOptionType(productInfo.getProductionOptions());

        if (files!=null && files.length > 0) {
            fileService.productImgUpload(files, productInfo.getId());
        }


        productInfo.getContentImgList().forEach(imgId->{
            fileMapper.updateProductFileId(productInfo.getId(),imgId);
        });

        return Response.getNewInstance().createResponseEntity("생성 완료", true);
    }

    public ResponseEntity<ResponseData> getProduct(Long id) {
        ProductModel productModel = productMapper.getProductModel(id);
        if (productModel == null) {
            return Response.getNewInstance().createResponseEntity("해당하는 상품이 없음", null);
        }
        return Response.getNewInstance().createResponseEntity("조회 완료", productModel);
    }

    public ResponseEntity<ResponseData> getProductList(HttpServletRequest request) {
        int totalCount = productMapper.getProductCount();
        List<ProductListModel> productListModelList = new ArrayList<>();
        if (totalCount > 0) {
            try {
                productListModelList = productMapper.getProductList(getPageParameter(request));
            } catch (NumberFormatException e) {
                return Response.getNewInstance()
                        .createErrorResponseEntity(INVALID_NO_INPUT_PAGE);
            }
        }

        Map map = new HashMap<>();
        map.put("totalCount", totalCount);
        map.put("productList", productListModelList);
        return Response.getNewInstance().createResponseEntity("성공", map);
    }

    private Map getPageParameter(HttpServletRequest request) {
        int pageNo = Integer.parseInt(request.getParameter("pageNo"));
        int pageSize = Integer.parseInt(request.getParameter("pageSize"));
        int offset = (pageNo - 1) * pageSize;
        Map map = new HashMap();
        map.put("pageSize", pageSize); //가져온 데이터에 키와 벨류값을 지정
        map.put("pageOffset", offset);
        return map;
    }

    public ResponseEntity<ResponseData> deleteProduct(Long productId){
        // 1.option_file -> productId 에 해당하는 db 및 파일 삭제
        // 2. study_option -> productId에 해당하는 id를 통해 study_option_type 의  옵션 아이디와 같은 데이터 삭제

        List<Long> optionsId=productMapper.getOptionId(productId);

        // 옵션 타입 삭제
        optionsId.forEach(optionType ->{
            productMapper.deleteOptionType(optionType);
        });

        // 옵션 삭제
        productMapper.deleteOption(productId);

        // 파일 삭제 및 file 데이터 삭제
        List<Long> fileIdList=fileMapper.getProductFileId(productId);
        fileIdList.forEach(fileId->{
            fileService.deleteFile(fileId);
        });
        fileMapper.deleteFile(productId);

        // 상품 삭제
        productMapper.deleteProduct(productId);

        return Response.getNewInstance().createResponseEntity("삭제 완료", "productModel");
    }
}