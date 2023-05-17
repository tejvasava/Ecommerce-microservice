package com.payment.consumer;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.Exception.MyInsufficiantBalance;
import com.payment.dto.Order;
import com.payment.dto.User;
import com.payment.entity.Payment;
import com.payment.repository.PaymentRepository;

@Component
public class OrderProcessingConsumer {

    public static final String USER_SERVICE_URL = "http://localhost:9093/users";
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private PaymentRepository repository;

    
    @KafkaListener(topics = "ORDER_PAYMENT_TOPIC",groupId="Payment-consumer-group")
    public void processOrder(@Payload String paymentRequest) throws IOException {
       
				  
    	 ObjectMapper mapper = new ObjectMapper();
    	 JsonNode data = new ObjectMapper().readTree(paymentRequest);
    	 
    	 String orderJson=data.toString();
    	  try
    	 {
    	    Order order= mapper.readValue(orderJson, Order.class);
    	 
    	    Payment payment=new Payment();
    	    payment.setAmount(order.getPrice());
    	    payment.setOrderId(order.getOrderId());
    	    payment.setPaidDate(null);
    	  
    	    payment.setPayMode(order.getPaymentMode());
    	    payment.setUserId(order.getUserId());
    	
    	    
    	    if( payment.getPayMode().equals("COD"))
    	    {
    	    	payment.setPaymentStatus("PENDING");
    	    }
    	    else{
                
    	    	User user = restTemplate.getForObject("http://localhost:9393/users" + "/" +payment.getUserId() , User.class);
    	    	
    	    	try {
    	    		 if (payment.getAmount() > user.getAvailableAmount()) {
    	                    //throw new RuntimeException("Insufficient amount !");
    	                	throw new MyInsufficiantBalance("you don't have sufficiant balance !!");
    	                } else {
    	                	payment.setPaymentStatus("PAID");
    	                }
    	    	}catch(MyInsufficiantBalance ex)
    	    	{
    	    		System.out.println("Exception :"+ex);
    	    	}
    	    	restTemplate.put("http://localhost:9393/users" + "/" +payment.getUserId() +"/"+payment.getAmount(), User.class);
            }
    	    repository.save(payment);
    	 }
    	  
			catch (IOException ex) {
				ex.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
       
}
