package com.lazeez.repository;

import com.lazeez.entity.EmailVerify;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EmailVeriyRepository extends MongoRepository<EmailVerify,String> {

    EmailVerify findByUserId(String id) ;

}
