package com.radwija.jumpstartbackend.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor
public class SendMailRequest {
    private String[] userIds;
    private String subject;
    private String text;
}
