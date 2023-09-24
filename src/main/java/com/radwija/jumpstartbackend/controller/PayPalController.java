package com.radwija.jumpstartbackend.controller;

import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.service.PayPalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/paypal")
public class PayPalController {
    @Autowired
    private PayPalService payPalService;

    @PostMapping("/init")
    public ResponseEntity<?> createPayment(@RequestParam("sum") String sumStr) {
        BigDecimal sum = new BigDecimal(sumStr);
        BaseResponse<?> response = payPalService.createPayment(sum);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/capture")
    public ResponseEntity<?> completePayment(@RequestParam("token") String token) {
        BaseResponse<?> response = payPalService.completePayment(token);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}
