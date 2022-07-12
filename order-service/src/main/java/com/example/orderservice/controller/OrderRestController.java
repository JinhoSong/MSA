package com.example.orderservice.controller;


import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order-service")
public class OrderRestController {

    private final OrderService orderService;
    private final Environment environment;

    @Autowired
    public OrderRestController(OrderService orderService, Environment environment) {
        this.orderService = orderService;
        this.environment = environment;
    }


    @GetMapping("/health_check")
    public String status(){
        return String.format("It's Working in User Service on PORT %s", environment.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId ,@RequestBody RequestOrder requestOrder){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto= modelMapper.map(requestOrder,OrderDto.class);
        orderDto.setUserId(userId);
        OrderDto createOrder =  orderService.createOrder(orderDto);

        ResponseOrder responseOrder = modelMapper.map(createOrder,ResponseOrder.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId){
        Iterable<OrderEntity> orderList = orderService.getOrderEntityByUserId(userId);
        List<ResponseOrder> result = new ArrayList<>();
        orderList.forEach( v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });


        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
