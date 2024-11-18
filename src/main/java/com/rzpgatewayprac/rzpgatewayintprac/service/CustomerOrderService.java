package com.rzpgatewayprac.rzpgatewayintprac.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.rzpgatewayprac.rzpgatewayintprac.dto.CustomerOrder;
import com.rzpgatewayprac.rzpgatewayintprac.repository.CustomerOrderRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CustomerOrderService {

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Value("${razorpay.key.id}")
    private String razorPayKey;

    @Value("${razorpay.secret.key}")
    private String razorPaySecret;

    private RazorpayClient client;
    public CustomerOrder createOrder(CustomerOrder customerOrder) throws RazorpayException {

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", customerOrder.getAmount() * 100); // amount in paisa
        this.client = new RazorpayClient(razorPayKey, razorPaySecret);

        // Create order in razorpay.
        Order razorPayOrder = client.orders.create(orderRequest);
        System.out.println(razorPayOrder);

        customerOrder.setRazorpayOrderId(razorPayOrder.get("id"));
        customerOrder.setOrderStatus(razorPayOrder.get("status"));

        customerOrderRepository.save(customerOrder);

        return customerOrder;
    }

}
