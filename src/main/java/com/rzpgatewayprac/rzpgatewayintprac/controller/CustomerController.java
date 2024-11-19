package com.rzpgatewayprac.rzpgatewayintprac.controller;

import com.rzpgatewayprac.rzpgatewayintprac.dto.CustomerOrder;
import com.rzpgatewayprac.rzpgatewayintprac.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/")
    private String init(){
        return "index";
    }

    @PostMapping(value = "/create-order", produces="application/json")
    @ResponseBody
    public ResponseEntity<CustomerOrder> createOrder(@RequestBody CustomerOrder customerOrder) throws Exception{
        CustomerOrder createdOrder = customerService.createOrder(customerOrder);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @PostMapping("/handle-payment-callback")
    public String handlePaymentCallback(@RequestParam Map<String, String> resPayLoad){
        System.out.println(resPayLoad);

        // Call service to update the order
        CustomerOrder updatedOrder = customerService.updateOrder(resPayLoad);

        // Check if the payment failed
        if ("PAYMENT_FAILED".equals(updatedOrder.getOrderStatus())) {
            return "paymentFailed"; // Show payment failed page or redirect to a failure view
        }

        return "success"; // Proceed with success flow
    }

}
