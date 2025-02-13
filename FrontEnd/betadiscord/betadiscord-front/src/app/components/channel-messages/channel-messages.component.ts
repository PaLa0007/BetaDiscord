import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from '../../services/message.service';
import { ChannelService } from '../../services/channel.service';
import { Message } from '../../models/message.model';

@Component({
  selector: 'app-channel-messages',
  templateUrl: './channel-messages.component.html',
  styleUrls: ['./channel-messages.component.css'],
})
export class ChannelMessagesComponent implements OnInit {
  channelId!: number;
  loggedInUserId!: number;
  messages: Message[] = [];
  newMessage = '';
  channelName = '';
  isEditingName = false;
  newChannelName = '';
  isAdminOrOwner = false; // ✅ Determines if user can edit the channel name

  constructor(
    private route: ActivatedRoute,
    private messageService: MessageService,
    private channelService: ChannelService
  ) { }

  ngOnInit(): void {
    this.loggedInUserId = Number(localStorage.getItem('userId'));

    // ✅ Ensure channelId is retrieved properly
    this.route.paramMap.subscribe(params => {
      this.channelId = Number(params.get('id'));

      if (!this.channelId || isNaN(this.channelId)) {
        console.error('❌ Invalid channel ID:', this.channelId);
        return;
      }

      console.log(`✅ Channel ID: ${this.channelId}, Logged-in User ID: ${this.loggedInUserId}`);

      this.fetchChannelName();
      this.fetchMessages();
      this.checkEditPermissions();
    });
  }

  fetchChannelName(): void {
    this.channelService.getChannelName(this.channelId).subscribe({
      next: (name: any) => { // ✅ Explicitly declare 'any' to prevent TypeScript errors
        console.log('✅ Channel Name API Response:', name);

        // ✅ Ensure name is a string and not an object
        if (typeof name === 'object' && name !== null && 'newName' in name) {
          this.channelName = (name as { newName: string }).newName;
        } else {
          this.channelName = String(name); // ✅ Ensure it's always a string
        }

        console.log('🎯 Updated Channel Name:', this.channelName);
      },
      error: (err) => console.error('❌ Failed to fetch channel name:', err)
    });
  }



  fetchMessages(): void {
    this.messageService.getMessagesForChannel(this.channelId).subscribe({
      next: (data: Message[]) => {
        console.log('✅ Service Response:', data);
        this.messages = [...data];
      },
      error: (err) => {
        console.error('❌ Error fetching messages:', err);
      },
    });
  }

  sendMessage(): void {
    if (this.newMessage.trim()) {
      console.log(`📤 Sending to: http://localhost:8165/api/channels/${this.channelId}/messages`);
      console.log(`📤 Message Content: ${this.newMessage}`);

      this.messageService.sendChannelMessage(this.channelId, this.newMessage).subscribe({
        next: () => {
          console.log('✅ Message Sent Successfully');
          this.fetchMessages(); // Refresh messages
          this.newMessage = '';
        },
        error: (err) => {
          console.error('❌ Error sending message:', err);
        },
      });
    }
  }

  // ✅ Check If User Can Edit Channel Name (Owner/Admin)
  checkEditPermissions(): void {
    this.channelService.getChannelUsers(this.channelId).subscribe({
      next: (users) => {
        this.isAdminOrOwner =
          users.owners.some(owner => owner.id === this.loggedInUserId) ||
          users.admins.some(admin => admin.id === this.loggedInUserId);
        console.log('🔍 Role Check - Can Edit:', this.isAdminOrOwner);
      },
      error: (err) => {
        console.error('❌ Failed to fetch users:', err);
      }
    });
  }

  // ✅ Start Editing Channel Name
  startEditingChannelName(): void {
    this.isEditingName = true;
    this.newChannelName = this.channelName;
  }

  // ❌ Cancel Editing
  cancelEditingChannelName(): void {
    this.isEditingName = false;
  }

  // ✅ Save Channel Name
  saveChannelName(): void {
    if (!this.newChannelName.trim()) {
      console.warn("⚠️ Channel name cannot be empty!");
      return;
    }
  
    console.log(`📡 Renaming channel to: ${this.newChannelName}`);
  
    this.channelService.changeChannelName(this.channelId, this.loggedInUserId, this.newChannelName).subscribe({
      next: () => {
        console.log(`✅ Channel renamed successfully`);
        this.channelName = this.newChannelName;
        this.isEditingName = false;
      },
      error: (err) => console.error(`❌ Failed to rename channel:`, err)
    });
  }
  
}
