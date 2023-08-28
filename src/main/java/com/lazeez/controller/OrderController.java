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


    @GetMapping("/getOrderByUserId")
    public ResponseEntity<?>  getOrderBYUserId(@RequestHeader("Authorization") String authorizationHeader)
    {
        return  orderService.getOrderBYUserId(authorizationHeader);

    }



    @PostMapping("/placeOrder")
    public  ResponseEntity<?>   placeOrder(@RequestHeader("Authorization") String authorizationHeader,@RequestBody OrderRequest orderRequest)
    {
        return orderService.placeOrder(authorizationHeader,orderRequest);

    }


    @DeleteMapping("/deleteOrder/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable String orderId)
    {

        return orderService.deleteOrder(orderId);

    }


    @GetMapping("/getAllOrder")
    public ResponseEntity<?> getAllOrders()
    {
        return orderService.getAllOrders();
    }



}
