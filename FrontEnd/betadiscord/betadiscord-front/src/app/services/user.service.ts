import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { UserDTO } from '../models/user.model';
import { catchError, tap } from 'rxjs/operators';


export interface Friend {
  id: number;
  name: string;
}

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private baseUrl = 'http://localhost:8165/api/users';

  constructor(private http: HttpClient) { }

  // ✅ Fetch Friends for a Specific User
  getFriendsForUser(userId: number): Observable<Friend[]> {
    return this.http.get<Friend[]>(`${this.baseUrl}/${userId}/friends`);
  }

  // 🔍 ✅ Search for Users (Used in "Add Friend" Modal)
  searchUsers(query: string): Observable<UserDTO[]> {
    const apiUrl = `http://localhost:8165/api/users/search?username=${query}`;
    console.log('📡 Calling Friend Search API:', apiUrl);

    return this.http.get<UserDTO[]>(apiUrl).pipe(
      tap(response => console.log('✅ Received Friend Search Results:', response)),
      catchError(err => {
        console.error('❌ Friend Search API Error:', err);
        return throwError(() => err);
      })
    );
  }

  // ➕ ✅ Add a Friend (Matches Backend Logic - No Friend Requests)
  addFriend(userId: number, friendId: number): Observable<UserDTO> {
    const apiUrl = `http://localhost:8165/api/users/${userId}/friends/${friendId}`; // Correct URL format

    console.log(`📡 Calling Add Friend API:`, apiUrl);

    return this.http.post<UserDTO>(apiUrl, {}).pipe(
      tap(response => console.log(`✅ Friend ${friendId} added to user ${userId}`, response)),
      catchError(err => {
        console.error(`❌ Add Friend API Error:`, err);
        return throwError(() => err);
      })
    );
  }

  // ❌ Remove Friend Functionality
  removeFriend(userId: number, friendId: number): Observable<void> {
    const apiUrl = `${this.baseUrl}/${userId}/friends/${friendId}`;
    console.log(`📡 Calling Remove Friend API:`, apiUrl);

    return this.http.delete<void>(apiUrl).pipe(
      tap(() => console.log(`✅ Friend ${friendId} removed from user ${userId}`)),
      catchError(err => {
        console.error(`❌ Remove Friend API Error:`, err);
        return throwError(() => err);
      })
    );
  }


}
