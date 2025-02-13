import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from '../../services/message.service';
import { Message } from '../../models/message.model';

@Component({
  selector: 'app-friend-messages',
  templateUrl: './friend-messages.component.html',
  styleUrls: ['./friend-messages.component.css'],
})
export class FriendMessagesComponent implements OnInit {
  friendId!: number;
  loggedInUserId!: number;
  messages: Message[] = [];
  newMessage = '';
  friendName = '';

  constructor(
    private route: ActivatedRoute,
    private messageService: MessageService,
    private cdRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loggedInUserId = Number(localStorage.getItem('userId'));

    // ✅ Detect when the route changes and reload chat
    this.route.paramMap.subscribe(params => {
      this.friendId = Number(params.get('id'));

      console.log(`Friend ID changed: ${this.friendId}, Logged-in User ID: ${this.loggedInUserId}`);

      this.fetchFriendName();
      this.fetchMessages();
    });
  }

  fetchFriendName(): void {
    this.messageService.getFriendName(this.friendId).subscribe({
      next: (name: string) => {
        console.log('✅ Friend’s Name:', name);
        this.friendName = name;
      },
      error: (err) => {
        console.error('❌ Error fetching friend’s name:', err);
        this.friendName = 'Unknown Friend';
      },
    });
  }

  fetchMessages(): void {
    this.messageService.getMessagesForFriend(this.loggedInUserId, this.friendId).subscribe({
      next: (data: Message[]) => {
        console.log('✅ Service Response:', data);
        this.messages = [...data];
        this.cdRef.detectChanges(); // ✅ Force UI update
      },
      error: (err) => {
        console.error('❌ Error fetching messages:', err);
      },
    });
  }

  sendMessage(): void {
    if (this.newMessage.trim()) {
      console.log(`Sending Message: ${this.newMessage}`);
      this.messageService.sendDirectMessage(this.loggedInUserId, this.friendId, this.newMessage).subscribe(() => {
        this.newMessage = ''; // ✅ Clear input field
        this.fetchMessages(); // ✅ Refresh chat
      });
    }
  }
}
