package com.shop.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ProductListModel {
    private int id;
    private String name;
    private int price;
    private int deliveryCost;
    private int point;
    @JsonIgnore
    private String imgJsonStr;
    public List<Object> imgList = new ArrayList();

    public void setImgJsonStr(String data) {
        imgList= (List<Object>) getObject(data,"imgList");
    }

    private Object getObject(String data, String key){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String,Object> map = objectMapper.readValue(data, Map.class);
            return (List<Object>) map.get(key);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
