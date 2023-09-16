package com.radwija.jumpstartbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailSenderConfig {
    @Bean
    public JavaMailSender getMail() {
        Properties properties = new Properties();
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        javaMailSender.setJavaMailProperties(properties);
        javaMailSender.setUsername("jumpstartecommerce.lithan@gmail.com");
        javaMailSender.setPassword("ovpeagzvdihzrubg");

        return javaMailSender;

    }
}
