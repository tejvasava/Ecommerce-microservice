package com.payment.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;


//@AllArgsConstructor
//@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Order {

	//private String id;
   // private String name;
   // private String category;
   // private String price;
   // private String purchaseDate;
  //  private String orderId;
   // private String userId;
   // private String paymentMode;
    
    
    private int id;
    private String name;
    private String category;
    private double price;
    private Date purchaseDate;
    private String orderId;
    private int userId;
    private String paymentMode;
    
}
