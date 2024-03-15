export interface ClientSubscription {
  id?: number;
  endpoint: string;
  expirationTime?: number;
  p256dh: string;
  auth: string;
}
