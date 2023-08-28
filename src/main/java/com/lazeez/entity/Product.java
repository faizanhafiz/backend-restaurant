package com.lazeez.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "products")
public class Product {


    @Id
    private String id;

    private  String productName;


    private String imageUrl;


    private double price;

    boolean isAvailable;





}
