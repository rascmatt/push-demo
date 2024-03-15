import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {ClientSubscription} from "../model/client-subscription";
import {ClientNotification} from "../model/client-notification";

@Injectable({
  providedIn: 'root'
})
export class SubscriptionService {
  private baseUrl = 'http://10.0.0.9:8080'; // Replace with your server's address

  constructor(private http: HttpClient) { }

  getPublicKey(): Observable<string> {
    return this.http.get(`${this.baseUrl}/public-key`, {
      responseType: 'text'
    });
  }

  saveSubscription(clientId: string, subscription: ClientSubscription): Observable<ClientSubscription> {
    return this.http.post<ClientSubscription>(`${this.baseUrl}/subscription/${clientId}`, subscription);
  }

  sendNotification(clientId: string, notification: ClientNotification): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/notification/${clientId}`, notification);
  }
}
