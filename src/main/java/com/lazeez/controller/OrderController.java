package com.lazeez.controller;


import com.lazeez.dto.OrderRequest;
import com.lazeez.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/order")
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;


    @GetMapping("/getOrder")
    public ResponseEntity<?>  getOrder(@RequestHeader("Authorization") String authorizationHeader)
    {
        return  orderService.getOrder(authorizationHeader);

    }



    @PostMapping("/placeOrder")
    public  ResponseEntity<?>   placeOrder(@RequestHeader("Authorization") String authorizationHeader)
    {
        return orderService.placeOrder(authorizationHeader );

    }


    @DeleteMapping("/deleteOrder/{orderId}")
    public ResponseEntity<?> deleteOrder(@RequestHeader("Authorization") String authorizationHeader,@PathVariable String orderId)
    {

        return orderService.deleteOrder(authorizationHeader,orderId);

    }


    @GetMapping("/getAllOrder")
    public ResponseEntity<?> getAllOrders()
    {
        return orderService.getAllOrders();
    }



}
