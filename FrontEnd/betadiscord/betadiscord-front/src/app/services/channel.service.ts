import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Channel } from '../models/channel.model'; // Use the Channel interface from the models folder
import { ChannelUsersDTO } from '../models/channel-users.model'; // ✅ Fix import path
import { UserDTO } from '../models/user.model'; // ✅ Adjust the import path based on your project structure
import { tap, catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';


import { map } from 'rxjs/operators'; // ✅ Import map


@Injectable({
  providedIn: 'root',
})
export class ChannelService {
  private baseUrl = 'http://localhost:8165/api/users';

  constructor(private http: HttpClient) { }

  getChannelsForUser(userId: number): Observable<Channel[]> {
    return this.http.get<Channel[]>(`${this.baseUrl}/${userId}/channels`);
  }

  getChannelName(channelId: number): Observable<string> {
    return this.http.get<{ name: string }>(`http://localhost:8165/api/channels/${channelId}/name`).pipe(
      map(response => response.name) // ✅ Ensure only the name is returned
    );
  }

  getChannelUsers(channelId: number): Observable<ChannelUsersDTO> {
    const url = `http://localhost:8165/api/channels/${channelId}/users`; // ✅ Corrected URL
    console.log(`📡 Correct API Call: ${url}`);

    return this.http.get<ChannelUsersDTO>(url);
  }

  changeUserRole(channelId: number, userId: number, targetUserId: number, newRole: string): Observable<any> {
    const url = `http://localhost:8165/api/channels/${channelId}/changeRole?userId=${userId}&targetUserId=${targetUserId}&newRole=${newRole}`; // ✅ Construct URL safely
    console.log(`📡 Changing User Role API Call: ${url}`); // ✅ Logs the exact request URL

    return this.http.post(url, {});
  }

  removeUserFromChannel(channelId: number, userId: number, removedUserId: number): Observable<any> {
    const url = `http://localhost:8165/api/channels/${channelId}/removeUser?userId=${userId}&removedUserId=${removedUserId}`;
    console.log(`📡 Removing User API Call: ${url}`);

    return this.http.post(url, {}); // ✅ Ensuring a POST request with an empty body
  }

  searchUsersForChannel(query: string, channelId: number): Observable<UserDTO[]> {
    const url = `http://localhost:8165/api/users/searchUsersForChannel?query=${query}&channelId=${channelId}`;
    console.log(`📡 Searching Users API Call: ${url}`); // ✅ Logs API Call
    return this.http.get<UserDTO[]>(url);
  }

  addUserToChannel(channelId: number, userId: number, addedUserId: number, role: string): Observable<any> {
    const url = `http://localhost:8165/api/channels/${channelId}/addUser?userId=${userId}&addedUserId=${addedUserId}&role=${role}`;
    console.log(`📡 Adding User API Call: ${url}`);
    return this.http.post(url, {});
  }

  changeChannelName(channelId: number, userId: number, newName: string): Observable<any> {
    const url = `http://localhost:8165/api/channels/${channelId}/changeName?userId=${userId}`;
    console.log(`📡 Changing Channel Name API Call: ${url}`);

    return this.http.post(url, newName, { responseType: 'text' }).pipe(
      tap((response: string) => console.log("✅ API Response:", response)),
      catchError((err) => {
        console.error("❌ Error changing channel name:", err);
        return throwError(() => err);
      })
    );
  }
  // 🗑️ **Delete Channel (New Method)**
  deleteChannel(channelId: number, userId: number): Observable<void> {
    const apiUrl = `http://localhost:8165/api/channels/${channelId}?userId=${userId}`;
    console.log(`🗑️ Calling Delete Channel API: ${apiUrl}`);

    return this.http.delete<void>(apiUrl).pipe(
      tap(() => console.log(`✅ Channel ${channelId} deleted successfully`)),
      catchError(err => {
        console.error(`❌ Failed to delete channel:`, err);
        return throwError(() => err);
      })
    );
  }

  createChannel(userId: number, channelName: string): Observable<Channel> {
    const apiUrl = `http://localhost:8165/api/channels/create?ownerId=${userId}`;
    console.log(`📡 Calling Create Channel API: ${apiUrl}`);

    return this.http.post<Channel>(apiUrl, channelName, { headers: { 'Content-Type': 'text/plain' } }).pipe(
      tap(response => console.log(`✅ Channel Created:`, response)),
      catchError(err => {
        console.error(`❌ Create Channel API Error:`, err);
        return throwError(() => err);
      })
    );
  }
}
