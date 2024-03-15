import {Component} from '@angular/core';
import {SwPush} from "@angular/service-worker";
import {SubscriptionService} from "./api/service/subscription.service";
import {ClientSubscription} from "./api/model/client-subscription";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'Push Notifications with Angular and Spring Boot';

  clientId: string = "";

  notificationMessage: string = "";
  notificationTitle: string = "";

  serverPublicKey?: string;

  constructor(
    private swPush: SwPush,
    private subscriptionService: SubscriptionService
  ) {
    console.log("App component initialized!");

    this.subscriptionService.getPublicKey()
      .subscribe(publicKey => this.serverPublicKey = publicKey);
  }

  subscribeToNotifications(clientId: string): void {

    if (!this.serverPublicKey) {
      alert("Could not subscribe to notifications: public key not available");
      console.error("Could not subscribe to notifications: public key not available");
      return;
    }

    this.swPush.requestSubscription({serverPublicKey: this.serverPublicKey})
      .then(sub => {

        console.log("Received subscription", sub);

        const s = {
          endpoint: sub.endpoint,
          expirationTime: sub.expirationTime,
          p256dh: sub.toJSON()?.keys?.['p256dh'],
          auth: sub.toJSON()?.keys?.['auth']
        } as ClientSubscription;

        this.subscriptionService.saveSubscription(clientId, s)
          .subscribe(() => console.log("Successfully subscribed to notifications"));

      })
      .catch(err => {
        alert("Could not subscribe to notifications " + err.message);
        console.error("Could not subscribe to notifications", err)
      });
  }

  sendNotification(clientId: string): void {
    this.subscriptionService.sendNotification(clientId, {
      title: this.notificationTitle,
      message: this.notificationMessage
    })
      .subscribe(() => console.log("Successfully sent notification"));
  }

}
