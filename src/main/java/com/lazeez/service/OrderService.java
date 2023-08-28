package com.lazeez.service;


import com.lazeez.dto.Cart;
import com.lazeez.dto.OrderRequest;
import com.lazeez.entity.Order;
import com.lazeez.entity.Product;
import com.lazeez.entity.User;
import com.lazeez.exceptions.CustomeException;
import com.lazeez.repository.OrderRepository;
import com.lazeez.repository.ProductRepository;
import com.lazeez.repository.UserRepository;
import com.lazeez.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private ProductRepository productRepository;
    Logger logger = LoggerFactory.getLogger(OrderService.class);
    public ResponseEntity<?> getOrder(String authorizationHeader) {

        try{

            User user = getUserByToken(authorizationHeader);
            List<Order> order =null;
            if(user!=null)
            {
                Sort sort = Sort.by(Sort.Direction.DESC, "orderDate");
                Pageable pageable = PageRequest.of(0, 10, sort);  // PageRequest with the first page and 10 items per page
                order = orderRepository.findByUserId(user.getId(), pageable);

            }


            return  ResponseEntity.ok(order);


        }catch (IllegalArgumentException ex)
        {

            logger.info("Order Id is null passed ===> ",ex);
            return new ResponseEntity<>("OrderId must not be null", HttpStatus.BAD_REQUEST);


        }

        catch (Exception ex)
        {

            logger.info("error occured inside  getOrderBYUserId ===> ",ex);
            return new ResponseEntity<>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);


        }

    }

    public ResponseEntity<?> placeOrder(String authorizationHeader) {

        try{

            User  user =null;
            if(!authorizationHeader.isEmpty())
            {
                user  = getUserByToken(authorizationHeader);
            }else{
                return new ResponseEntity<>("{\"message\":\""+"Token must not empty"+"\"}",HttpStatus.BAD_REQUEST);
            }
            if(user!=null)
            {

                List<Cart> cartList = user.getCart();
                if(!cartList.isEmpty())
                {

                    Order order = Order.builder().
                            user(user)
                            .status("PENDING")
                            .orderDate(new Date())
                            .product(cartList).
                            build();
                   Order confirmOrder = orderRepository.save(order);
                   if(confirmOrder!=null)
                   {
                       return  ResponseEntity.ok("{\"message\":\""+"Order is placed Successfully"+"\"}");

                   }else{
                       return new  ResponseEntity<>("{\"message\":\""+"Order is not  placed Successfully"+"\"}",HttpStatus.BAD_REQUEST);

                   }




                }else{
                    return  new ResponseEntity<>("{\"message\":\""+"cart is empty"+"\"}",HttpStatus.BAD_REQUEST);
                }

            }else{
                return  new ResponseEntity<>("{\"message\":\""+"User not found"+"\"}",HttpStatus.BAD_REQUEST);


            }


        }catch (Exception ex)
        {
            logger.info("Exception in increaseCartItem",ex);
            return  new ResponseEntity<>("Server Error",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> deleteOrder(String authorizationHeader,String orderId) {

        try
        {
            User user = getUserByToken(authorizationHeader);
            if(user!=null)
            {
                Optional<Order> order = orderRepository.findById(orderId);
                if(order.isPresent())
                {
                   orderRepository.delete(order.get());
                   return  ResponseEntity.ok("{\"message\":\"order deleted\""+"\"}");

                }else{
                    return  ResponseEntity.ok("{\"message\":\"order not present\""+"\"}");

                }
            }else{
                return new ResponseEntity<>("{\"message\":\"user not found \""+"\"}",HttpStatus.BAD_REQUEST);
            }


        }catch (Exception ex)
        {
            logger.info("error occured inside  deleteOrder ===> ",ex);
            return new ResponseEntity<>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);


        }
    }

    public ResponseEntity<?> getAllOrders() {

        try{


            return ResponseEntity.ok(orderRepository.findAll());

        }catch (Exception ex)
        {
            logger.info("error occured inside  getAllOrders  ===> ",ex);
            return new ResponseEntity<>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);


        }
    }


    private User getUserByToken(String authorizationHeader)
    {
        // Extract the token from the authorization header
        String token = authorizationHeader.replace("Bearer ", "");

        if(token.isEmpty())
        {
            throw new CustomeException("token is empty");
        }



        String email = jwtUtil.extractUsername(token);
        User  user = userRepository.findByEmail(email);
        if(user!=null)
        {
            return  user;
        }

        return null;


    }
}
