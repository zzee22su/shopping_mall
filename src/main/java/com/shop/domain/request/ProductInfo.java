package com.shop.domain.request;
import lombok.Data;
import java.util.List;

@Data
public class ProductInfo {
 private Long id;
 private String name;
 private int price;
 private int deliveryCost;
 private int point;
 private String category;
 private String content;
 private Long fileGroupId;
 private List<ProductionOption> productionOptions;
 private List<Long> contentImgList;
 private List<Long> deleteImgFileList;

 @Data
 public static class ProductionOption {
  private Long productId;
  private Long id;
  private String name;
  private List<OptionValue> optionValues;
 }

 @Data
 public static class OptionValue{
  private Long optionTypeId;
  private String type;
  private String price;
 }
}
