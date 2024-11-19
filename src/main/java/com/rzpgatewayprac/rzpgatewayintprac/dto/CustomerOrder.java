package com.rzpgatewayprac.rzpgatewayintprac.dto;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name="customer_order")
public class CustomerOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;
    private String name;
    private String email;
    private Long contact;
    private String category;
    private Integer amount;
    private String orderStatus;
    private String razorpayOrderId;
}
