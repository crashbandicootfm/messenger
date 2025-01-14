import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {MessageRequest} from '../models/request/message-request.model';
import {MessageResponse} from '../models/response/message-response.model';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  private readonly url = "http://localhost:8080/api/v1/messages/";

  constructor(private http: HttpClient) {}

  sendMessage(messageRequest: MessageRequest): Observable<MessageResponse> {
    const token = localStorage.getItem('token');
    console.log("Token from service:", token);

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    });

    return this.http.post<MessageResponse>(this.url, messageRequest, { headers });
  }
}
