package com.shop.domain.request;
import lombok.Data;
import java.util.List;

@Data
public class ProductInfo {
 private int id;
 private String name;
 private int price;
 private int deliveryCost;
 private int point;
 private String content;
 private List<ProductionOption> productionOptions;

 @Data
 public static class ProductionOption {
  private int productId;
  private int id;
  private String name;
  private List<OptionValue> optionValues;
 }

 @Data
 public static class OptionValue{
  private int optionTypeId;
  private String type;
  private String price;
 }
}
