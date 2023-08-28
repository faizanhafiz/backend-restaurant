package com.lazeez.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderRequest {



   // delivered , pending , failed ,orderConfirmed,on the way
   private  String status;

   private  List<String> productIds;


}
