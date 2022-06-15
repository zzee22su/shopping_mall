package com.shop.domain.model;

import lombok.Data;

@Data
public class ProductListModel {
    private int id;
    private String name;
    private int price;
    private int deliveryCost;
    private int point;
}
