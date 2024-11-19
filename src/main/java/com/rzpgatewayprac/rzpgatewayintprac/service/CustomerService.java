package com.rzpgatewayprac.rzpgatewayintprac.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.rzpgatewayprac.rzpgatewayintprac.exception.SignatureVerificationException;
import com.rzpgatewayprac.rzpgatewayintprac.dto.CustomerOrder;
import com.rzpgatewayprac.rzpgatewayintprac.repository.CustomerOrderRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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

        // Create order in Razorpay
        Order razorPayOrder = client.orders.create(orderRequest);
        System.out.println(razorPayOrder);

        customerOrder.setRazorpayOrderId(razorPayOrder.get("id"));
        customerOrder.setOrderStatus(razorPayOrder.get("status"));

        customerOrderRepository.save(customerOrder);

        return customerOrder;
    }

    public CustomerOrder updateOrder(Map<String, String> responsePayLoad) throws SignatureVerificationException {
        String razorPayOrderId = responsePayLoad.get("razorpay_order_id");
        String razorPayPaymentStatus = responsePayLoad.get("razorpay_payment_status");
        String razorPayPaymentId = responsePayLoad.get("razorpay_payment_id");
        String razorPaySignature = responsePayLoad.get("razorpay_signature");

        // Verify the signature to ensure the authenticity of the payment
        String generatedSignature = generatePaymentSignature(responsePayLoad, razorPaySecret);
        if (!razorPaySignature.equals(generatedSignature)) {
            // Throw custom exception if signature doesn't match
            throw new SignatureVerificationException("Invalid signature. Signature mismatch.");
        }

        // Optional: Log the response to confirm structure
        System.out.println("razorPayOrderId Payload: " + razorPayOrderId);
        System.out.println("razorPayPaymentStatus Payload: " + razorPayPaymentStatus);
        System.out.println("Response Payload: " + responsePayLoad);

        CustomerOrder order = customerOrderRepository.findByRazorpayOrderId(razorPayOrderId);

        // Check payment status and update order accordingly
        if ("success".equalsIgnoreCase(razorPayPaymentStatus)) {
            order.setOrderStatus("PAYMENT_COMPLETED");
        } else {
            order.setOrderStatus("PAYMENT_COMPLETED");
        }

        // Save the updated order status
        return customerOrderRepository.save(order);
    }

    // Method to manually generate signature
    private String generatePaymentSignature(Map<String, String> responsePayLoad, String secretKey) throws SignatureVerificationException {
        String paymentId = responsePayLoad.get("razorpay_payment_id");
        String orderId = responsePayLoad.get("razorpay_order_id");

        // Construct the string to hash
        String stringToHash = orderId + "|" + paymentId;

        try {
            // Perform HMAC-SHA256 hash with the secret key
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(stringToHash.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes); // Encode the hash in base64
        } catch (NoSuchAlgorithmException e) {
            throw new SignatureVerificationException("Error generating signature: " + e.getMessage());
        }
    }
}
