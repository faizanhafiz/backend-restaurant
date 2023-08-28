package com.lazeez.repository;

import com.lazeez.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends MongoRepository<User,String> {

    User findByEmail(String email);

    User findByMobile(Long mobile);

    User findByEmailIgnoreCase(String email);
}
