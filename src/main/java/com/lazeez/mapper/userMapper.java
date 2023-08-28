package com.lazeez.mapper;


import com.lazeez.dto.Cart;
import com.lazeez.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface userMapper {



//    @Mapping(source = "cart.productId" ,target = "id")
//    @Mapping(source = "cart.productName",)
//    @Mapping(source = "cart.imageUrl")
//    @Mapping(source = "cart.price")
    List<Product>   cartListProductList(List<Cart> cartList);
}
