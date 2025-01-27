import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ChatRequest} from '../models/request/chat-request.model';
import {ChatResponse} from '../models/response/chat-response.model';

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private readonly url = "http://localhost:8080/api/v1/chats/";

  constructor(private http: HttpClient) {}

  createChat(name: string): Observable<any> {
    const token = localStorage.getItem('token');
    console.log("Token from service:", token);

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    });

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
}
