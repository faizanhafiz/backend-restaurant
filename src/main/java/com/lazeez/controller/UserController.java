package com.lazeez.controller;


import com.lazeez.dto.EmailVerifyDto;
import com.lazeez.dto.LoginRequest;
import com.lazeez.entity.User;
import com.lazeez.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RequestMapping("/user")
@RestController

public class UserController {




    @Autowired
    private UserService userService;





    @CrossOrigin("*")
    @PostMapping("/addUser")
    public ResponseEntity<?>  addUser(@RequestBody User user){


        return  userService.addUser(user);

    }

    @CrossOrigin("*")
    @PutMapping("/updateUser/")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String authorizationHeader, @RequestBody User user){
        return userService.updateUser( authorizationHeader,user);

    }

    @CrossOrigin("*")
    @DeleteMapping("/deleteUser")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String authorizationHeader){

        return userService.deleteUser(authorizationHeader);

    }


    @PostMapping("/verifyEmail")
    public ResponseEntity<?>  verifyEmail(@RequestBody EmailVerifyDto emailVerifyDto)
    {
        return userService.verifyEmail(emailVerifyDto);
    }

    @CrossOrigin("*")
    @GetMapping("/getUser")
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String authorizationHeader)
    {

        return userService.getUser(authorizationHeader);

    }


    @CrossOrigin("*")
    @GetMapping("/getAllUser")
    public ResponseEntity<?> getAllUser(){

        return userService.getAllUser();

    }

    @CrossOrigin("*")
    @PostMapping("/addToCart/{productid}")
    public ResponseEntity<?>  addToCart(@RequestHeader("Authorization") String authorizationHeader,@PathVariable  String productid)
    {
        return userService.addToCart(authorizationHeader,productid);
    }

    @CrossOrigin("*")
    @GetMapping("/getCart")
    public ResponseEntity<?>  getCart(@RequestHeader("Authorization") String authorizationHeader)
    {

        return userService.getCart(authorizationHeader);


    }


    @CrossOrigin("*")
    @PostMapping("/login")
    public  ResponseEntity<?> login(@RequestBody LoginRequest loginRequest)
    {

        System.out.println("inside login controller");
        return userService.login(loginRequest);




    }

    @CrossOrigin("*")
    @GetMapping("/getUserData")

    public ResponseEntity<?> getUserData(@RequestHeader("Authorization") String authorizationHeader){

        return  userService.getUserData(authorizationHeader);

    }


    @CrossOrigin("*")
    @PutMapping("/incrCartItem/{productId}")
    public ResponseEntity<?> increaseCartItem(@RequestHeader("Authorization") String authorizationHeader ,@PathVariable  String productId){

        return  userService.increaseCartItem(authorizationHeader,productId);

    }


    @CrossOrigin("*")
    @PutMapping("/decCartItem/{productId}")
    public ResponseEntity<?> decreasecartItem(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String productId)
    {
              return   userService.decreasecartItem(authorizationHeader, productId);
    }


    @CrossOrigin("*")
    @PutMapping("/checkout")
    public ResponseEntity<?> checkOut(@RequestHeader("Authorization") String authorizationHeader)
    {


        return  userService.checkOut(authorizationHeader);
    }




}
