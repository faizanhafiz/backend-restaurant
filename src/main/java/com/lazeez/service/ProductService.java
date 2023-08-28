package com.lazeez.service;


import com.lazeez.entity.Product;
import com.lazeez.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProductService {


      Logger logger  = LoggerFactory.getLogger(ProductService.class);
    @Autowired
    private ProductRepository productRepository;
    public ResponseEntity<?>  getAllproducts() {
         try {

             return  ResponseEntity.ok(productRepository.findAll());

         }catch (Exception ex){


             logger.info("error occured in getAllProduct ====>", ex);

             return  new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);



         }
    }

    public ResponseEntity<?> saveProduct(Product request) {

        try{

            Product product = productRepository.save(request);

            if(product!=null){

                return ResponseEntity.ok("product is Sucessfully added");

            }else{

                return  new ResponseEntity<>("product not Found",HttpStatus.NOT_FOUND);

            }

        }catch (Exception ex)
        {
            logger.info("error occured inside saveProduct ===>" ,ex);

            return  new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }


    public ResponseEntity<?> updateProduct(String productId, Product product) {

        Product productExist = productRepository.findById(productId).get();
        if(productExist!=null)
        {
            product.setId(productId);

            Product updateProduct = productRepository.save(product);
            if(updateProduct!=null)
            {
                return  ResponseEntity.ok("product is updated");
            }else{

                return  new ResponseEntity<>("Bad Request",HttpStatus.BAD_REQUEST);

            }

        }else{
                    return new ResponseEntity<>("Product not Found",HttpStatus.NOT_FOUND);

        }

    }

    public ResponseEntity<?> deleteProduct(String productId) {
        try{
              productRepository.deleteById(productId);

              return new ResponseEntity<>("Product Deleted",HttpStatus.OK);


        }catch (IllegalArgumentException ex){

            logger.info("error occured inside deleteProduct ===>",ex);

            return  new ResponseEntity<>("product Id must not be null",HttpStatus.BAD_REQUEST);

        }


    }


}
