import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Message } from '../models/message.model';

@Injectable({
  providedIn: 'root',
})
export class MessageService {
  private baseUrl = 'http://localhost:8165/api/messages';
  private loggedInUserId: number; // ✅ Add logged-in user ID


  constructor(private http: HttpClient) {
    this.loggedInUserId = Number(localStorage.getItem('userId')); // ✅ Get user ID from localStorage
  }

  // ✅ Fetch messages for a specific friend
  getMessagesForFriend(userId: number, friendId: number): Observable<Message[]> {
    return this.http.get<any[]>(`${this.baseUrl}/private?senderId=${userId}&receiverId=${friendId}`).pipe(
      map(messages =>
        messages.map(message => ({
          id: message.id,
          sender: { 
            id: message.senderId, 
            username: message.senderId === userId ? 'You' : 'Friend' // ✅ Correctly assign username
          },
          content: message.content,
          timestamp: message.timestamp,
        }))
      )
    );
  }

  // ✅ Send a direct message to a friend
  sendDirectMessage(userId: number, friendId: number, content: string): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/send`, {
      senderId: userId,
      receiverId: friendId,
      content: content,
    });
  }

  // ✅ Fetch a friend's name
  getFriendName(friendId: number): Observable<string> {
    return this.http.get<{ name: string }>(`http://localhost:8165/api/users/${friendId}/name`).pipe(
      map(response => response.name)
    );
  }

  // ✅ Fetch messages for a specific channel
  getMessagesForChannel(channelId: number): Observable<Message[]> {
    return this.http.get<any[]>(`http://localhost:8165/api/channels/${channelId}/messages`).pipe(
      map(messages =>
        messages.map(message => ({
          id: message.id,
          sender: { 
            id: message.senderId, 
            username: message.senderUsername || `User ${message.senderId}` // ✅ Use real username
          },
          content: message.content,
          timestamp: message.timestamp,
        }))
      )
    );
  }
  
  // ✅ Send a message in a channel
  sendChannelMessage(channelId: number, content: string): Observable<any> {
    const senderId = Number(localStorage.getItem('userId')); // ✅ Get senderId from storage
    console.log(`📤 Sending Channel Message to: http://localhost:8165/api/channels/${channelId}/messages?senderId=${senderId}`);
  
    return this.http.post<any>(
      `http://localhost:8165/api/channels/${channelId}/messages?senderId=${senderId}`, // ✅ Add senderId
      content,
      { headers: { 'Content-Type': 'text/plain' } }
    );
  }
  
  
     
}
