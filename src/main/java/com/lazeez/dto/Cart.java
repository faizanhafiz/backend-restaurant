package com.lazeez.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Cart {
    private String id;

    private  String productName;


    private String imageUrl;


    private double price;
    private int quantity;

    private String productId;


}
