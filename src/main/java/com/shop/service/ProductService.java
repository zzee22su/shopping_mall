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
import java.util.stream.Stream;

import static com.shop.response.ErrorCode.INVALID_NO_INPUT_PAGE;
import static com.shop.response.ErrorCode.INVALID_UPDATE_ID;

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

        // 상품 디비에 추가
        int value = productMapper.insertProduct(productInfo);

        insertOption(productInfo);

        if (files != null && files.length > 0) {
            fileService.productImgUpload(files, productInfo.getId());
        }

        if (productInfo.getContentImgList() != null) {
            productInfo.getContentImgList().forEach(imgId -> {
                fileMapper.updateProductFileId(productInfo.getId(), imgId);
            });
        }

        return Response.getNewInstance().createResponseEntity("생성 완료", true);
    }

    @Transactional(propagation = Propagation.REQUIRED, transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseEntity<ResponseData> updateProduct(ProductInfo productInfo, MultipartFile[] files) {
        if (productInfo.getId() == null) {
            return Response.getNewInstance()
                    .createErrorResponseEntity(INVALID_UPDATE_ID);
        }

        if (productMapper.getProductModel(productInfo.getId()) == null) {
            return Response.getNewInstance().createResponseEntity("수정 하려고 하는 상품이 없습니다.", false);
        }

        // 상품 내용 업데이트
        int value = productMapper.updateProduct(productInfo);

        // 옵션 내용 삭제 후 다시 생성 (좋은 방법인가 ??)
        deleteOption(productInfo.getId());
        insertOption(productInfo);

        // MultipartFile 파일이 있을 경우 파일 업로드
        if (files != null && files.length > 0) {
            fileService.productImgUpload(files, productInfo.getId());
        }

        //deleteImgFileList 있을 경우 study_file 테이블에서 삭제
        List<Long> getDeleteImgFileList = productInfo.getDeleteImgFileList();
        if (getDeleteImgFileList != null) {
            getDeleteImgFileList.forEach(fileId -> {
                if (fileMapper.isExistsFileInProduct(productInfo.getId(), fileId)) {
                    fileMapper.deleteFile(fileId);
                    fileService.deleteFile(fileId);
                }
            });
        }

        //contentImgList 상품 아이디에서 content필드의 값이 1인 찾아서 contentImgList와 비교하여 없는 값은 삭제 해야 하고 있는 값은 추가  (좋은 방법인가 ??)
        List<Long> inputContentImgList = productInfo.getContentImgList();
        List<Long> contentImgList = fileMapper.getContentImgId(productInfo.getId());


        // 상품정보 수정에서 새로 추가된 이미지로 study_file에 contentimg 값을 1로 변경
        Stream<Long> addContentImg = differenceArray(inputContentImgList, contentImgList);
        addContentImg.forEach(id -> {
            if (fileMapper.getFilePath(id) != null) {
                fileMapper.updateProductFileId(productInfo.getId(), id);
            }
        });

        // front의 편집기 컨첸츠내용에서 삭제 된 사항으로 db와 파일을 제거
        Stream<Long> removeContentImg = differenceArray(contentImgList, inputContentImgList);
        removeContentImg.forEach(id -> {
            if (fileMapper.isExistsFileInProduct(productInfo.getId(), id)) {
                fileMapper.deleteFile(id);
                fileService.deleteFile(id);
            }
        });

        //System.out.println(productInfo.getId());
        return Response.getNewInstance().createResponseEntity("수정 완료", true);
    }

    public ResponseEntity<ResponseData> getProduct(Long id) {
        ProductModel productModel = productMapper.getProductModel(id);
        if (productModel == null) {
            return Response.getNewInstance().createResponseEntity("해당하는 상품이 없음", null);
        }
        return Response.getNewInstance().createResponseEntity("조회 완료", productModel);
    }

    public ResponseEntity<ResponseData> getProductList(HttpServletRequest request) {
        int totalCount = productMapper.getProductCount(request.getParameter("category"));
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
        map.put("category", request.getParameter("category"));
        return map;
    }

    public ResponseEntity<ResponseData> deleteProduct(Long productId) {
        // 1.option_file -> productId 에 해당하는 db 및 파일 삭제
        // 2. study_option -> productId에 해당하는 id를 통해 study_option_type 의  옵션 아이디와 같은 데이터 삭제

        deleteOption(productId);

        // 파일 삭제 및 file 데이터 삭제
        List<Long> fileIdList = fileMapper.getProductFileId(productId);
        fileIdList.forEach(fileId -> {
            fileService.deleteFile(fileId);
        });
        fileMapper.deleteProductFiles(productId);

        // 상품 삭제
        productMapper.deleteProduct(productId);

        return Response.getNewInstance().createResponseEntity("삭제 완료", "productModel");
    }

    private void deleteOption(Long productId) {
        List<Long> optionsId = productMapper.getOptionId(productId);

        // 옵션 타입 삭제
        optionsId.forEach(optionType -> {
            productMapper.deleteOptionType(optionType);
        });

        // 옵션 삭제
        productMapper.deleteOption(productId);
    }

    private void insertOption(ProductInfo productInfo) {
        productInfo.getProductionOptions().forEach(productionOption -> {
                    productionOption.setProductId(productInfo.getId());
                }
        );


        int value = productMapper.insertOption(productInfo.getProductionOptions());
        productInfo.getProductionOptions().forEach(productionOption -> {
                    productionOption.getOptionValues().forEach(optionValue -> {
                        optionValue.setOptionTypeId(productionOption.getId());
                    });
                }
        );
        value = productMapper.insertOptionType(productInfo.getProductionOptions());
    }

    // 배열의 차집합을 구한다.
    private Stream<Long> differenceArray(List<Long> inputArray, List<Long> orgArray) {
        return inputArray.stream().filter(id -> {
            return !orgArray.contains(id);
        });
    }

}