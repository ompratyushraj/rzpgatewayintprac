package com.rzpgatewayprac.rzpgatewayintprac.controller;

import com.rzpgatewayprac.rzpgatewayintprac.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@RestController
public class RazorpayWebhookController {

    @Autowired
    private CustomerService customerService;

    @RequestMapping(value = "/razorpay-webhook", method = RequestMethod.POST)

    public void handleWebhook(@RequestBody Map<String, String> responsePayload) {
        try {
            // Pass the payload to update the order in the service
            customerService.updateOrder(responsePayload);
        } catch (Exception e) {
            // Handle exception if the payload processing fails
            e.printStackTrace();
        }
    }
}
