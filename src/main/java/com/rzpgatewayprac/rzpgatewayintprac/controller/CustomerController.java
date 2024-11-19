package com.rzpgatewayprac.rzpgatewayintprac.controller;

import com.rzpgatewayprac.rzpgatewayintprac.dto.CustomerOrder;
import com.rzpgatewayprac.rzpgatewayintprac.service.CustomerService;
import com.razorpay.SignatureVerificationException;
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
    private String init() {
        return "index";
    }

    @PostMapping(value = "/create-order", produces="application/json")
    @ResponseBody
    public ResponseEntity<CustomerOrder> createOrder(@RequestBody CustomerOrder customerOrder) throws Exception {
        CustomerOrder createdOrder = customerService.createOrder(customerOrder);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @PostMapping("/handle-payment-callback")
    public String handlePaymentCallback(@RequestParam Map<String, String> resPayLoad) {
        // Debugging logs to print the received payload
        System.out.println("Received payload: " + resPayLoad);

        // Extract the payment status
        String paymentStatus = resPayLoad.get("razorpay_payment_status");
        System.out.println("Payment Status: " + paymentStatus);

        if (paymentStatus == null) {
            System.out.println("Payment status is missing in the payload");
        }

        try {
            // Handle payment update after signature verification
            CustomerOrder updatedOrder = customerService.updateOrder(resPayLoad);

            // Handle order status based on payment outcome
            if ("PAYMENT_FAILED".equals(updatedOrder.getOrderStatus())) {
                return "paymentFailed"; // Show payment failed page
            }

            return "success"; // Redirect to success page
        } catch (SignatureVerificationException e) {
            System.out.println("Signature verification failed: " + e.getMessage());
            return "paymentFailed"; // Redirect to failure page on verification error
        }
    }
}
