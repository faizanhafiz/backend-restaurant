package com.lazeez.dto;


import com.lazeez.entity.Address;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDatadto {
    private String userName;

    private String profileUrl ;

    private Long mobile;

    private Address address;

    private String email;
}
