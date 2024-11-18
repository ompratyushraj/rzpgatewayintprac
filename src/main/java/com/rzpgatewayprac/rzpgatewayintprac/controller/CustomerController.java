package com.rzpgatewayprac.rzpgatewayintprac.controller;

import com.rzpgatewayprac.rzpgatewayintprac.service.CustomerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomerController {

    @Autowired
    private CustomerOrderService customerOrderService;

    @GetMapping("/")
    private String init(){
        return "index";
    }
}
