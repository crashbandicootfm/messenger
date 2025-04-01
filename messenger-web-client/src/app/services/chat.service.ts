import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {ChatRequest} from '../models/request/chat-request.model';
import {ChatResponse} from '../models/response/chat-response.model';
import {ChatWithPasswordRequest} from '../models/request/chat-with-password-request.model';
import {switchMap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private readonly url = "http://localhost:8080/api/v1/chats/";

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    console.log("Token from service:", token);
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    });
  }

  createChat(name: string): Observable<ChatResponse> {
    const headers = this.getHeaders();
    const payload: ChatRequest = { name };

    return this.http.post<ChatResponse>(this.url, payload, { headers });
  }

  joinChat(name: string): Observable<ChatResponse> {
    const token = localStorage.getItem('token');
    console.log("Token from service:", token);
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    });

    const payload = { name };

    return this.http.post<ChatResponse>(`${this.url}join`, payload, { headers });
  }

  createChatWithPassword(name: string, password: string): Observable<ChatResponse> {
    const headers = this.getHeaders();
    const payload: ChatWithPasswordRequest = { name, password };
    return this.http.post<ChatResponse>(`${this.url}createWithPassword`, payload, { headers });
  }

  joinChatWithPassword(name: string, password: string): Observable<ChatResponse> {
    const headers = this.getHeaders();
    const payload = { name };
    return this.http.post<ChatResponse>(`${this.url}join-with-password?password=${password}`, payload, { headers });
  }

  getUserChats(): Observable<ChatResponse[]> {
    const headers = this.getHeaders();
    return this.http.get<ChatResponse[]>(`${this.url}user/chats?timestamp=${Date.now()}`, { headers });
  }

  createPrivateChat(otherUserId: number): Observable<ChatResponse> {
    const headers = this.getHeaders();
    return this.http.post<ChatResponse>(`${this.url}start-private?otherUserId=${otherUserId}`, {}, { headers });
  }

  deleteChat(chatId: number): Observable<any> {
    const headers = this.getHeaders();
    return this.http.delete(`${this.url}${chatId}`, { headers });
  }

  leaveChat(chatId: number | null): Observable<any> {
    if (chatId === null) {
      return throwError(() => new Error('Chat ID is null'));
    }

    const headers = this.getHeaders();
    return this.http.post(`${this.url}leave?chatId=${chatId}`, {}, { headers });
  }

  getMessagesByChatId(chatId: number): Observable<any> {
    const headers = this.getHeaders();
    return this.http.get<any>(`${this.url}${chatId}/messages`, { headers });
  }

  getUnreadMessagesCount(chatId: number, userId: number): Observable<number> {
    return this.http.get<number>(`${this.url}unread-count?chatId=${chatId}&userId=${userId}`);
  }

  markChatAsRead(chatId: number, userId: number | null): Observable<void> {
    return this.http.post<void>(`${this.url}${chatId}/mark-as-read`, { userId });
  }
}
