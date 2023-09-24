package com.radwija.jumpstartbackend.controller;

import com.paypal.core.PayPalHttpClient;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/paypal")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PayPalHttpClient payPalHttpClient;

    @PostMapping("/init")
    public ResponseEntity<?> createPayment(@RequestParam("sum") String sumStr) {
        BigDecimal sum = new BigDecimal(sumStr);
        BaseResponse<?> response = transactionService.createPayment(sum);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // TODO: use this end point to trigger complete order or cancel
    // complete = execute this end point
    // cancel = find out token expiration or set null token in database
    @PostMapping("/capture")
    public ResponseEntity<?> completePayment(@RequestParam("token") String token) {
        BaseResponse<?> response = transactionService.completePayment(token);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}
