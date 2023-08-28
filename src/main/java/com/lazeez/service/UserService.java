package com.lazeez.service;


import com.lazeez.config.EmailService;
import com.lazeez.dto.*;
import com.lazeez.entity.EmailVerify;
import com.lazeez.entity.Product;
import com.lazeez.entity.User;
import com.lazeez.exceptions.CustomeException;
import com.lazeez.repository.EmailVeriyRepository;
import com.lazeez.repository.ProductRepository;
import com.lazeez.repository.UserRepository;
import com.lazeez.security.JwtUtil;
import com.lazeez.security.UserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserDetailService userDetailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EmailVeriyRepository emailVeriyRepository;


    @Autowired
    EmailService emailService;


    Logger logger = LoggerFactory.getLogger(UserService.class);
    public   ResponseEntity<?> updateUser(String authorizationHeader , User user) {
        try{

            User  user1  = getUserByToken(authorizationHeader);

            user.setId(user1.getId());

            userRepository.save(user);

            return ResponseEntity.ok("User Update");

        }catch (IllegalArgumentException ex){

                logger.info("error occured inside updateUser",ex);

                 return new ResponseEntity<>("user Id must not be null", HttpStatus.BAD_REQUEST);
        }catch (Exception ex){

            logger.info("error occured inside updateUser",ex);

            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    public ResponseEntity<?> deleteUser(String authorizationHeader) {
        try {
            // Extract the token from the authorization header
            String token = authorizationHeader.replace("Bearer ", "");

            System.out.println("token"+"    "+token);

            String email = jwtUtil.extractUsername(token);
            UserDetails userDetails;
            if(!email.isEmpty())
            {
                 userDetails =userDetailService.loadUserByUsername(email);
            }else{
                return new ResponseEntity<>("User not there",HttpStatus.BAD_REQUEST);

            }

            System.out.println("inside delete========="+email);


            // Validate the token
            if (jwtUtil.validateToken(token,userDetails))
            {
                // Extract user information from the token
                String username = jwtUtil.extractUsername(token);

                // Find the user by email (assuming email is the identifier)
                User user = userRepository.findByEmail(username);

                if (user != null) {
                    userRepository.deleteById(user.getId()); // Assuming user.getId() is the user's identifier
                    return ResponseEntity.ok("User deleted");
                } else {
                    return new ResponseEntity<>("User not Found", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>("Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }
        } catch (IllegalArgumentException ex) {
            logger.info("error occurred inside deleteUser", ex);
            return new ResponseEntity<>("Authorization header must not be null", HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            logger.info("error occurred inside deleteUser", ex);
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    public   ResponseEntity<?> getUser(String authorizationHeader) {

        try
        {
            User user = getUserByToken(authorizationHeader);


            if(user!=null)
            {
                return ResponseEntity.ok(user);

            }else {
                return  new ResponseEntity<>("User not Found",HttpStatus.BAD_REQUEST);
            }

        }catch (IllegalArgumentException ex){

            logger.info("error occured inside getUser",ex);

            return new ResponseEntity<>("user Id must not be null", HttpStatus.BAD_REQUEST);
        }

        catch (Exception ex){
            logger.info("error occured inside getUser",ex);
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    public  ResponseEntity<?> getAllUser() {

        try
        {
            List<User> users  = userRepository.findAll();
            return ResponseEntity.ok(users);

        }catch (Exception ex)
        {
            logger.info("error occured inside getAllUser",ex);
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    public ResponseEntity<?> addUser(User user) {

        try{

            User userExist = userRepository.findByEmailIgnoreCase(user.getEmail());
            User userExist2 = userRepository.findByMobile(user.getMobile());
            if((userExist !=null) || (userExist2 !=null))
            {

                return new ResponseEntity<>("User Already present",HttpStatus.BAD_REQUEST);


            }else{


              user.setPassword(passwordEncoder.encode(user.getPassword()));

               User addedUser = userRepository.save(user);


               if(addedUser!=null)
               {

                   String otp = generateOTP();

                   EmailVerify emailVerify = EmailVerify.builder()
                           .userId(addedUser.getId())
                           .otp(otp)
                           .build();


                   emailVeriyRepository.save(emailVerify);


                   emailService.sendSimpleMessage(user.getEmail(),"Verify your Email","You OTP for Email Verifications is:  "+otp);



                   return  ResponseEntity.ok("{\"message\":\""+"OTP sent to "+ user.getEmail()+"\"}");

               }else{
                   return  new ResponseEntity<>("something went wrong",HttpStatus.BAD_REQUEST);
                }
            }

        }catch (IllegalArgumentException ex){

            logger.info("error occured inside addUser",ex);

            return new ResponseEntity<>("user  must not be null", HttpStatus.BAD_REQUEST);
        }

        catch (Exception ex)
        {
            logger.info("error occured inside addUser",ex);
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    public ResponseEntity<?> addToCart(String authorizationHeader,String productId ) {
        try
        {

            User user = getUserByToken(authorizationHeader);
            Product product = productRepository.findById(productId).get();
            int total =0;

            if(product==null)
            {
                return new ResponseEntity<>("{\"message\":\""+"Product not Found"+"\"}",HttpStatus.BAD_REQUEST);

            }

            if(user!=null)
            {
                if(!product.isAvailable())
                {
                    return  new ResponseEntity<>("{\"message\":\""+"item not available"+"\"}",HttpStatus.BAD_REQUEST);
                }
                List<Cart>  cartList = user.getCart();
                if(cartList!=null)
                {

                    for (Cart cart : cartList)
                    {
                        System.out.println("Inside==============");

                        if (cart.getId().equals(productId)) {




                            cart.setQuantity(cart.getQuantity() + 1);

                            user.setCartTotal(user.getCartTotal()+product.getPrice());

                            logger.info(("Cart quantity is increased"));


                            userRepository.save(user);

                            return ResponseEntity.ok("{\"message\":\"product added \""+"\"}");


                        }


                    }
                    total+=product.getPrice();
                    cartList.add( Cart.builder().id(product.getId())
                            .productName(product.getProductName())
                            .price(product.getPrice())
                            .imageUrl(product.getImageUrl())
                            .quantity(1).build());
                    user.setCartTotal(user.getCartTotal()+product.getPrice());
                    userRepository.save(user);
                    return ResponseEntity.ok("{\"message\":\""+product.getProductName()+"\"is Added to Cart\""+"\"}");

                }
                Cart cart = Cart.builder().id(product.getId())
                        .productName(product.getProductName())
                        .price(product.getPrice())
                        .imageUrl(product.getImageUrl())
                        .quantity(1).build();

                ArrayList<Cart>  oneProduct = new ArrayList<>();
                oneProduct.add(cart);
                user.setCart(oneProduct);
                user.setCartTotal(user.getCartTotal()+product.getPrice());

                userRepository.save(user);
                return ResponseEntity.ok("{\"message\":\""+product.getProductName()+"\"is Added to Cart\""+"\"}");



            }else{
                logger.info("User not Found");
                return new ResponseEntity<>("{\"message\":\""+"User not Found"+"\"}", HttpStatus.BAD_REQUEST);
            }

        }catch (Exception ex){
            logger.info("error inside addtoCart",ex);
            return new ResponseEntity<>("Something went wrong ",HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    public ResponseEntity<?> getCart(String authorizationHeader) {
        try{


           User user = getUserByToken(authorizationHeader);
            List<Cart> cart = new ArrayList<>();
           if(user!=null)
           {
               cart  = user.getCart();
           }else{
               return  new ResponseEntity<>("{\"message\":\""+"User not Found"+"\"}",HttpStatus.BAD_REQUEST);
           }


            return ResponseEntity.ok(cart);



        }catch (Exception ex)
        {
            logger.info("Exception occured in getcart method",ex);
            return new ResponseEntity<>("Something went wrong" ,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> login(LoginRequest loginRequest) {


        System.out.println("Inside login service");
        System.out.println(loginRequest.getEmail());
        System.out.println(loginRequest.getPassword());
        try{

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));

        }catch (Exception ex)
        {
            return  new   ResponseEntity<>("{\"message\":\""+"User not Exist"+"\"}",HttpStatus.BAD_REQUEST);


        }

        User user  = userRepository.findByEmailIgnoreCase(loginRequest.getEmail());
        System.out.println("User email "+user.getEmail());
        if(!user.isActive())
        {
            return  new ResponseEntity<>("{\"message\":\""+"Please Verify your Account"+"\"}", HttpStatus.BAD_REQUEST);

        }
        UserDetails userDetails= this.userDetailService.loadUserByUsername(loginRequest.getEmail());
        System.out.println(userDetails.getUsername());
        System.out.println(userDetails.getPassword());

        String token = jwtUtil.generateToken(userDetails);
        JwtResponse jwtResponse = new JwtResponse(token);

        System.out.println("Token  "+token);

        return ResponseEntity.status(HttpStatus.OK).body(jwtResponse);

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
        User  user = userRepository.findByEmailIgnoreCase(email);
        if(user!=null)
        {
            return  user;
        }

        return null;


    }


    private  String generateOTP() {
        String characters = "0123456789"; // Characters used for generating OTP
        SecureRandom random = new SecureRandom();

        StringBuilder otpBuilder = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            int randomIndex = random.nextInt(characters.length());
            otpBuilder.append(characters.charAt(randomIndex));
        }

        return otpBuilder.toString();
    }

    public ResponseEntity<?> verifyEmail(EmailVerifyDto emailVerifyDto) {

      try{
          User  user = userRepository.findByEmailIgnoreCase(emailVerifyDto.getEmail());
          EmailVerify emailVerify = emailVeriyRepository.findByUserId(user.getId());
          if(emailVerify.getOtp().equals(emailVerifyDto.getOtp()))
          {
              user.setActive(true);
              userRepository.save(user);
              return  ResponseEntity.ok("{\"message\":\""+"Your Email has been Verified"+"\"}");
          }else{
              return  ResponseEntity.ok("Please enter correct otp");
          }

      }catch (Exception ex)
      {

          logger.info("Exception in verifyEmail",ex);
          return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }


    public ResponseEntity<?> getUserData(String authorizationHeader) {
        try{

            User  user =null;
            if(!authorizationHeader.isEmpty())
            {
                user  = getUserByToken(authorizationHeader);
            }else{
                 return new ResponseEntity<>("{\"message\":\""+"Token must not empty"+"\"}",HttpStatus.BAD_REQUEST);
            }

            UserDatadto userDatadto ;
            if(user!=null)
            {
                userDatadto=    UserDatadto.builder().userName(user.getUserName())
                        .address(user.getAddress())
                        .mobile(user.getMobile())
                        .profileUrl(user.getProfileUrl())
                        .email(user.getEmail())
                        .build();
            }else{
                return new ResponseEntity<>( "{\"message\":\""+"user not found"+"\"}",HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.ok(userDatadto);




        }catch (Exception ex)
        {

            logger.info("Exception in getUserData",ex);
           throw new CustomeException("Exception occur in getUserData");

        }
    }

    public ResponseEntity<?> increaseCartItem(String authorizationHeader,String productId) {
        try{
            User  user =null;
            if(!authorizationHeader.isEmpty())
            {
                user  = getUserByToken(authorizationHeader);
            }else{
                return new ResponseEntity<>("{\"message\":\""+"Token must not empty"+"\"}",HttpStatus.BAD_REQUEST);
            }
            if(user==null)
            {
                return new ResponseEntity<>("{\"message\":\""+"user not found"+"\"}",HttpStatus.BAD_REQUEST);

            }

            List<Cart> cartList = user.getCart();
            Optional<Product> product1 = productRepository.findById(productId);
            System.out.println(productId);
            Product product =null;
            if(product1.isPresent()){
                product =  product1.get();
            }
            if(product!=null)
            {
              if(!cartList.isEmpty())
              {
                  for(Cart cartProduct : cartList){
                      if(cartProduct.getId().equals(product.getId()))
                      {
                          cartProduct.setQuantity(cartProduct.getQuantity()+1);
                          user.setCartTotal(user.getCartTotal()+cartProduct.getPrice());
                          user.setCart(cartList);
                          userRepository.save(user);
                          return ResponseEntity.ok("{\"message\":\""+"Item quantity Increased"+"\"}");
                      }

                  }
              }
            }

        return  new ResponseEntity<>("went wrong",HttpStatus.BAD_REQUEST);


        }catch (Exception ex)
        {
            logger.info("Exception in increaseCartItem",ex);
            return  new ResponseEntity<>("Server Error",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> decreasecartItem(String authorizationHeader,String productId) {

        try{
            User  user =null;
            if(!authorizationHeader.isEmpty())
            {
                user  = getUserByToken(authorizationHeader);
            }else{
                return new ResponseEntity<>("{\"message\":\""+"Token must not empty"+"\"}",HttpStatus.BAD_REQUEST);
            }

            List<Cart> cartList = user.getCart();
            Product product = productRepository.findById(productId).get();
            if(product!=null)
            {
                if(!cartList.isEmpty())
                {
                    for(Cart cartProduct : cartList){
                        if(cartProduct.getId().equals(product.getId()))
                        {
                            if(cartProduct.getQuantity()==1)
                            {

                                user.setCartTotal(user.getCartTotal()-cartProduct.getPrice());
                                cartList.remove(cartProduct);
                                user.setCart(cartList);
                                userRepository.save(user);
                                return ResponseEntity.ok("{\"message\":\""+"Item  quantity Decrease"+"\"}");
                            }else{
                                cartProduct.setQuantity(cartProduct.getQuantity()-1);
                                user.setCartTotal(user.getCartTotal()-cartProduct.getPrice());
                                user.setCart(cartList);
                                userRepository.save(user);
                                return ResponseEntity.ok("{\"message\":\""+"Item  quantity Decrease"+"\"}");
                            }

                        }

                    }
                }
            }

            return  new ResponseEntity<>("went wrong",HttpStatus.BAD_REQUEST);


        }catch (Exception ex)
        {
            logger.info("Exception in increaseCartItem",ex);
            return  new ResponseEntity<>("Server Error",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> checkOut(String authorizationHeader) {
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
                        //////



                    return null;





//                     start from here




                    //////////
                }else{
                    return  new ResponseEntity<>("cart is empty",HttpStatus.BAD_REQUEST);
                }

            }
            return null;

        }catch (Exception ex)
        {
            logger.info("Exception in increaseCartItem",ex);
            return  new ResponseEntity<>("Server Error",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

