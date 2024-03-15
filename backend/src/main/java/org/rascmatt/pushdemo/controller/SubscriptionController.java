package org.rascmatt.pushdemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Urgency;
import org.apache.http.HttpResponse;
import org.jose4j.lang.JoseException;
import org.rascmatt.pushdemo.dto.NotificationDTO;
import org.rascmatt.pushdemo.dto.SubscriptionDTO;
import org.rascmatt.pushdemo.dto.payload.ActionDTO;
import org.rascmatt.pushdemo.dto.payload.PayloadDTO;
import org.rascmatt.pushdemo.entity.Subscription;
import org.rascmatt.pushdemo.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionRepository subscriptionRepository;
    private final PushService pushService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${vapid.public-key}")
    private String publicKey;

    @GetMapping(value = "/public-key", produces = "text/plain")
    public ResponseEntity<String> getPublicKey() {
        return ResponseEntity.ok(publicKey);
    }

    @PostMapping("/subscription/{clientId}")
    public ResponseEntity<Subscription> saveSubscription(@PathVariable String clientId,
                                                         @RequestBody SubscriptionDTO subscription) {
        final Subscription s = map(subscription);
        s.setClientId(clientId);
        s.setCreatedAt(Instant.now());
        final Subscription savedSubscription = subscriptionRepository.save(s);
        return new ResponseEntity<>(savedSubscription, HttpStatus.CREATED);
    }

    @PostMapping("/notification/{clientId}")
    public ResponseEntity<Void> sendNotification(@PathVariable String clientId,
                                                 @RequestBody NotificationDTO notification) throws GeneralSecurityException, JoseException, IOException, ExecutionException, InterruptedException {
        Optional<Subscription> subscription = subscriptionRepository.findFirstByClientIdOrderByCreatedAtDesc(clientId);

        if (subscription.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        final PayloadDTO payload = PayloadDTO.builder()
                .notification(org.rascmatt.pushdemo.dto.payload.NotificationDTO.builder()
                        .title(notification.getTitle())
                        .body(notification.getMessage())
                        .icon(null)
                        .vibrate(List.of(100, 50, 100))
                        .data(null)
                        .actions(List.of(ActionDTO.builder()
                                .action("explore")
                                .title("Go to the site")
                                .build()))
                        .build())
                .build();

        final Subscription s = subscription.get();
        final Notification pushNotification = new Notification(s.getEndpoint(), s.getP256dh(), s.getAuth(),
                objectMapper.writeValueAsBytes(payload));

        final HttpResponse response = pushService.send(pushNotification);

        if (response.getStatusLine().getStatusCode() != 201) {
            log.error("Failed to send notification: {}", StreamUtils.copyToString(response.getEntity().getContent(), StandardCharsets.UTF_8));
            return ResponseEntity.status(response.getStatusLine().getStatusCode()).build();
        }

        return ResponseEntity.noContent().build();
    }

    private Subscription map(SubscriptionDTO s) {
        Subscription subscription = new Subscription();
        subscription.setEndpoint(s.getEndpoint());
        subscription.setExpirationTime(s.getExpirationTime());
        subscription.setP256dh(s.getP256dh());
        subscription.setAuth(s.getAuth());
        return subscription;
    }

    private SubscriptionDTO map(Subscription s) {
        SubscriptionDTO subscription = new SubscriptionDTO();
        subscription.setEndpoint(s.getEndpoint());
        subscription.setExpirationTime(s.getExpirationTime());
        subscription.setP256dh(s.getP256dh());
        subscription.setAuth(s.getAuth());
        return subscription;
    }
}