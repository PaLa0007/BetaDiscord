import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private baseUrl = 'http://localhost:8165/api/users'; // ✅ Use correct backend URL

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<{ id: number; token: string }> {
    return this.http.post<{ id: number; token: string }>(`${this.baseUrl}/login`, { username, password });
  }
}
