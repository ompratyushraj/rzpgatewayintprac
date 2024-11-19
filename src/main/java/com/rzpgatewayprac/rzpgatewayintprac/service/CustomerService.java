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

import java.util.Map;

@Service
public class CustomerService {

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
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", customerOrder.getEmail());

        this.client = new RazorpayClient(razorPayKey, razorPaySecret);

        // Create order in razorpay.
        Order razorPayOrder = client.orders.create(orderRequest);
        System.out.println(razorPayOrder);

        customerOrder.setRazorpayOrderId(razorPayOrder.get("id"));
        customerOrder.setOrderStatus(razorPayOrder.get("status"));

        customerOrderRepository.save(customerOrder);

        return customerOrder;
    }

    public CustomerOrder updateOrder(Map<String, String> responsePayLoad) {
        String razorPayOrderId = responsePayLoad.get("razorpay_order_id");
        String razorPayPaymentStatus = responsePayLoad.get("razorpay_payment_status");

        // Optional: Log the response to confirm structure
        System.out.println("razorPayOrderId Payload: " + razorPayOrderId);
        System.out.println("razorPayPaymentStatus Payload: " + razorPayPaymentStatus);
        System.out.println("Response Payload: " + responsePayLoad);

        CustomerOrder order = customerOrderRepository.findByRazorpayOrderId(razorPayOrderId);

        // Check payment status and update order accordingly
        if ("success".equalsIgnoreCase(razorPayPaymentStatus)) {
            order.setOrderStatus("PAYMENT_COMPLETED");
        } else {
            order.setOrderStatus("PAYMENT_FAILED");
        }

        System.out.println("Response Payload: " + responsePayLoad);

        // Save the updated order status
        CustomerOrder updatedOrder = customerOrderRepository.save(order);

        return updatedOrder;
    }


}
