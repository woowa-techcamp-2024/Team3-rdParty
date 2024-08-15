package com.thirdparty.ticketing.domain.payment;

import com.thirdparty.ticketing.domain.payment.dto.PaymentRequest;

public interface PaymentProcessor {
    void processPayment(PaymentRequest paymentRequest);
}
