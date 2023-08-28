package com.lazeez.entity;


import com.lazeez.dto.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Document(collection = "order")
public class Order {


    @Id
    private String id;

    private  User user;

    private String status;

   private Date orderDate;

    private List<Cart> product;




}
