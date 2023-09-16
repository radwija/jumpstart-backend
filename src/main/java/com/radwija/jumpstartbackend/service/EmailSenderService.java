package com.radwija.jumpstartbackend.service;

import com.radwija.jumpstartbackend.payload.request.SendMailRequest;


public interface EmailSenderService {
    void sendMail(String to, String subject, String body);
    void sendMailToUsers(SendMailRequest sendMailRequest, String username);
}

