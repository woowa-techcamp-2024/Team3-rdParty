package com.thirdparty.ticketing.jpa.payment;

import com.thirdparty.ticketing.jpa.payment.dto.PaymentRequest;

public interface PaymentProcessor {
    void processPayment(PaymentRequest paymentRequest);
}
