package org.rascmatt.pushdemo.dto;

import lombok.Data;

@Data
public class SubscriptionDTO {

    private Long id;
    private String endpoint;
    private Long expirationTime;
    private String p256dh;
    private String auth;
}
