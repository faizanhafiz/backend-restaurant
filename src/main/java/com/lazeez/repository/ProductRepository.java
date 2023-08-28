package com.lazeez.repository;


import com.lazeez.dto.ResponseDto;
import com.lazeez.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends MongoRepository<Product,String> {


     List<ResponseDto> findByProductName(String pId);
}
