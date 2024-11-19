package com.rzpgatewayprac.rzpgatewayintprac.repository;

import com.rzpgatewayprac.rzpgatewayintprac.dto.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Integer> {

    CustomerOrder findByRazorpayOrderId(String razorPayOrderId);
}
