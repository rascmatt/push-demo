package org.rascmatt.pushdemo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String clientId;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private String endpoint;

    private Long expirationTime;

    @Column(nullable = false)
    private String p256dh;

    @Column(nullable = false)
    private String auth;
}