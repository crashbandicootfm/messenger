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

  private getHeaders(): HttpHeaders {
    const token = sessionStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  sendMessage(messageRequest: MessageRequest): Observable<MessageResponse> {
    const token = sessionStorage.getItem('token');
    console.log("Token from service:", token);

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    });

    return this.http.post<MessageResponse>(this.url, messageRequest, { headers });
  }

  getMessagesByChatId(chatId: number): Observable<MessageResponse[]> {
    const token = sessionStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    });

    return this.http.get<MessageResponse[]>(`${this.url}chat/${chatId}`, { headers });
  }

  deleteMessage(messageId: number): Observable<void> {
    const token = sessionStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    });

    return this.http.delete<void>(`${this.url}mess/${messageId}`, { headers });
  }

  markMessageAsRead(messageId: number): Observable<void> {
    const token = sessionStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    });

    return this.http.post<void>(`${this.url}mess/${messageId}/read`, {}, { headers });
  }

  uploadFile(file: File): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', file, file.name);

    const headers = this.getHeaders();

    return this.http.post<any>(`${this.url}upload-file`, formData, { headers });
  }

  getFile(fileId: string): Observable<Blob> {
    const headers = this.getHeaders();
    return this.http.get(`${this.url}get-file/${fileId}`, { headers, responseType: 'blob' });
  }
}
