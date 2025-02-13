import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from '../../services/message.service';
import { Message } from '../../models/message.model';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-message-list',
  templateUrl: './message-list.component.html',
  styleUrls: ['./message-list.component.css'],
})
export class MessageListComponent implements OnInit, OnDestroy {
  channelId!: number;
  friendId!: number;
  messages: Message[] = [];
  newMessage = '';
  friendName = '';
  pollingSubscription!: Subscription;

  constructor(
    private route: ActivatedRoute,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    const routeId = this.route.snapshot.paramMap.get('id');
    const routePath = this.route.snapshot.routeConfig?.path;

    if (routePath?.startsWith('channels')) {
      this.channelId = Number(routeId);
      if (this.channelId) {
        this.startPolling(() => this.fetchMessagesForChannel(this.channelId));
      }
    } else if (routePath?.startsWith('friends')) {
      this.friendId = Number(routeId);
      if (this.friendId) {
        this.fetchFriendName(this.friendId);
        this.startPolling(() => this.fetchMessagesForFriend(this.friendId));
      }
    }
  }

  fetchMessagesForChannel(channelId: number): void {
    this.messageService.getMessagesForChannel(channelId).subscribe({
      next: (data: Message[]) => {
        this.messages = data;
      },
      error: (err) => {
        console.error('Error fetching messages:', err);
      },
    });
  }

  fetchMessagesForFriend(friendId: number): void {
    const userId = Number(localStorage.getItem('userId'));
    this.messageService.getMessagesForFriend(userId, friendId).subscribe({
      next: (data: Message[]) => {
        this.messages = data;
      },
      error: (err) => {
        console.error('Error fetching messages:', err);
      },
    });
  }

  fetchFriendName(friendId: number): void {
    this.messageService.getFriendName(friendId).subscribe({
      next: (name: string) => {
        this.friendName = name;
      },
      error: (err) => {
        console.error('Error fetching friend name:', err);
        this.friendName = 'Unknown Friend';
      },
    });
  }

  sendMessage(): void {
    if (this.newMessage.trim()) {
      if (this.channelId) {
        this.messageService.sendChannelMessage(this.channelId,  this.newMessage).subscribe(() => {
          this.fetchMessagesForChannel(this.channelId);
          this.newMessage = '';
        });
      } else if (this.friendId) {
        const userId = Number(localStorage.getItem('userId'));
        this.messageService.sendDirectMessage(userId, this.friendId, this.newMessage).subscribe(() => {
          this.fetchMessagesForFriend(this.friendId);
          this.newMessage = '';
        });
      }
    }
  }

  startPolling(fetchFunction: () => void): void {
    this.pollingSubscription = interval(5000).subscribe(() => {
      fetchFunction();
    });
  }

  ngOnDestroy(): void {
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
    }
  }
}
