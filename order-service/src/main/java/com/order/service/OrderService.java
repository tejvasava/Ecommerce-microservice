package com.order.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.dto.OrderResponseDTO;
import com.order.dto.Payment;
import com.order.dto.PaymentDTO;
import com.order.dto.UserDTO;
import com.order.entity.Order;
import com.order.repository.OrderRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Service
@RefreshScope
@Slf4j
public class OrderService {

    public static final String ORDER_SERVICE = "orderService";
    @Autowired
    private OrderRepository repository;

    @Autowired
    @Lazy
    private RestTemplate restTemplate;

  
      public static final String fetchPaymentUri = "http://localhost:9292/payments";
    
	 // @Value("${microservice.payment-service.endpoints.fetchPaymentById.uri}")
	  //private String fetchPaymentUri;
	  
	  public static final String fetchUserUri = "http://localhost:9093/users";
	  
	 // @Value("${microservice.user-service.endpoints.fetchUserById.uri}") 
	 // private String fetchUserUri;
	  
	  @Value("${order.producer.topic.name}")
	  private String orderTopic;
    
   // @Autowired
   // private StreamBridge streamBridge;
    
    @Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

    public String placeAnOrder(Order order) {
        //save a copy in order-service DB

        order.setPurchaseDate(new Date());
        order.setOrderId(UUID.randomUUID().toString().split("-")[0]);
        repository.save(order);
        //kafkaTemplate.send(orderTopic, new ObjectMapper().writeValueAsString(order));
		kafkaTemplate.send(orderTopic, order);
         // streamBridge.send("orderBinding-out-0", new ObjectMapper().writeValueAsString(order));
        return "Your order with (" + order.getOrderId() + ") has been placed ! we will notify once it will confirm";
    }

	@CircuitBreaker(name = ORDER_SERVICE, fallbackMethod = "getOrderDetails")
	public OrderResponseDTO getOrder(String orderId) throws JsonProcessingException 
	{
		// own DB -> ORDER

		System.out.println("fetchPaymentUri : " + fetchPaymentUri + " && " + "fetchUserUri: " + fetchUserUri);
		
		log.info("OrderService::getOrder request fetch order by orderId {} ", orderId);
		
		Order order = repository.findByOrderId(orderId);

		// PAYMENT-> REST call payment-service
		//PaymentDTO paymentDTO = restTemplate.getForObject("http://localhost:9292/payments" + orderId, PaymentDTO.class);
		
		PaymentDTO paymentDTO =new PaymentDTO();
		
		Payment payment = restTemplate.getForObject("http://localhost:9292/payments/"+ orderId, Payment.class);
		
		paymentDTO.setAmount(payment.getAmount());
		paymentDTO.setPaidDate(payment.getPaidDate());
		paymentDTO.setPaymentStatus(payment.getPaymentStatus());
		paymentDTO.setPayMode(payment.getPayMode());
		
		log.info("OrderService::getOrder fetching payment response from payment service {} ",
				new ObjectMapper().writeValueAsString(paymentDTO));

		// user-info-> rest call user-service order.getUserId()
		UserDTO userDTO = restTemplate.getForObject(fetchUserUri + order.getUserId(), UserDTO.class);
		log.info("OrderService::getOrder fetching user response  from user service {} ",new ObjectMapper().writeValueAsString(userDTO));

		return OrderResponseDTO.builder().order(order).paymentResponse(paymentDTO).userInfo(userDTO).build();

	}
	
	public OrderResponseDTO getOrderDetails(String orderId, Exception ex) {
		//you can call a DB 
		//you can invoke external api 
		return new  OrderResponseDTO("FAILED", null, null, null); 
	    }


}
