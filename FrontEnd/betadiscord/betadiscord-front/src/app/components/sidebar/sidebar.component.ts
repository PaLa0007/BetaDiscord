import { Component, OnInit, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { ChannelService } from '../../services/channel.service';
import { UserService } from '../../services/user.service';
import { Channel } from '../../models/channel.model';
import { Friend } from '../../models/friend.model';
import { UserDTO } from '../../models/user.model';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css'],
})
export class SidebarComponent implements OnInit {
  channels: Channel[] = [];
  friends: Friend[] = [];

  // ✅ Friend Search Modal
  isFriendSearchModalOpen = false;
  friendSearchQuery = '';
  friendSearchResults: UserDTO[] = [];

  // ✅ Create Channel Modal
  isCreateChannelModalOpen = false;
  newChannelName = '';

  constructor(
    private channelService: ChannelService,
    private userService: UserService,
    private router: Router
  ) { }

  ngOnInit(): void {
    const userId = Number(localStorage.getItem('userId'));
    if (userId) {
      this.fetchChannels(userId);
      this.fetchFriends(userId);
    }
  }

  fetchChannels(userId: number): void {
    this.channelService.getChannelsForUser(userId).subscribe({
      next: (data: Channel[]) => {
        this.channels = data;
      },
      error: (err) => {
        console.error('❌ Error fetching channels:', err);
      },
    });
  }

  fetchFriends(userId: number): void {
    this.userService.getFriendsForUser(userId).subscribe({
      next: (data: Friend[]) => {
        this.friends = data;
      },
      error: (err) => {
        console.error('❌ Error fetching friends:', err);
      },
    });
  }

  goToChannel(channelId: number): void {
    this.router.navigate([`/channels/${channelId}`]);
  }

  goToFriend(friendId: number): void {
    this.router.navigate([`/friends/${friendId}`]);
  }

  logout(): void {
    localStorage.removeItem('userId');
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }

  // ✅ Open Friend Search Modal
  openFriendSearchModal(): void {
    this.isFriendSearchModalOpen = true;
    this.friendSearchQuery = '';
    this.friendSearchResults = [];
  }

  // ❌ Close Friend Search Modal
  closeFriendSearchModal(): void {
    this.isFriendSearchModalOpen = false;
  }

  // 🔍 Search Users for Adding as Friends
  searchFriends(): void {
    console.log('🔍 searchFriends() triggered with query:', this.friendSearchQuery);

    if (!this.friendSearchQuery.trim()) {
      this.friendSearchResults = []; // ✅ Clear results if query is empty
      console.log('⚠️ Empty query, clearing results.');
      return;
    }

    this.userService.searchUsers(this.friendSearchQuery).subscribe({
      next: (results: UserDTO[]) => {
        console.log('✅ Friend Search API Response:', results);

        // ✅ Prevent duplicates by resetting the array first
        this.friendSearchResults = results.map(user => ({
          ...user,
          isAlreadyFriend: this.isAlreadyFriend(user.id)
        }));

        console.log('🛠️ Updated friendSearchResults:', this.friendSearchResults);
      },
      error: (err) => {
        console.error('❌ Failed to search friends:', err);
      }
    });
  }

  // ✅ Check if a user is already a friend
  isAlreadyFriend(userId: number): boolean {
    return this.friends.some(friend => friend.id === userId);
  }

  // ✅ Add Friend Functionality
  addFriend(userId: number): void {
    const loggedInUserId = Number(localStorage.getItem('userId'));

    if (!loggedInUserId) {
      console.error('❌ No logged-in user found.');
      return;
    }

    console.log(`➕ Adding Friend - User ${userId} to User ${loggedInUserId}`);

    this.userService.addFriend(loggedInUserId, userId).subscribe({
      next: () => {
        console.log(`✅ Successfully added Friend ${userId}`);
        this.fetchFriends(loggedInUserId); // Refresh Friends List
        this.closeFriendSearchModal();
      },
      error: (err) => {
        console.error(`❌ Failed to add Friend ${userId}:`, err);
      }
    });
  }

  // ✅ Variables for Context Menu
  isFriendContextMenuOpen = false;
  contextMenuPosition = { x: 0, y: 0 };
  selectedFriend: Friend | null = null;

  // ✅ Open Right-Click Context Menu for Friend
  handleFriendRightClick(event: MouseEvent, friend: Friend): void {
    event.preventDefault();
    this.selectedFriend = friend;
    this.isFriendContextMenuOpen = true;
    this.contextMenuPosition = { x: event.clientX, y: event.clientY };
  }

  // ✅ Close Context Menu on Click Outside
  @HostListener('document:click')
  closeFriendContextMenu(): void {
    this.isFriendContextMenuOpen = false;
  }

  // ✅ Remove Friend Action
  removeFriend(friendId: number): void {
    const loggedInUserId = Number(localStorage.getItem('userId'));

    if (!loggedInUserId) {
      console.error('❌ No logged-in user found.');
      return;
    }

    console.log(`❌ Removing Friend - User ${friendId} from User ${loggedInUserId}`);

    this.userService.removeFriend(loggedInUserId, friendId).subscribe({
      next: () => {
        console.log(`✅ Successfully removed Friend ${friendId}`);
        this.friends = this.friends.filter(friend => friend.id !== friendId); // Update UI instantly
        this.isFriendContextMenuOpen = false; // Close menu
      },
      error: (err) => {
        console.error(`❌ Failed to remove Friend ${friendId}:`, err);
      }
    });
  }

  // ✅ Open Create Channel Modal
  openCreateChannelModal(): void {
    this.isCreateChannelModalOpen = true;
    this.newChannelName = '';
  }

  // ❌ Close Create Channel Modal
  closeCreateChannelModal(): void {
    this.isCreateChannelModalOpen = false;
  }

  // 🚀 Create Channel Functionality
  createChannel(): void {
    const loggedInUserId = Number(localStorage.getItem('userId'));

    if (!loggedInUserId || !this.newChannelName.trim()) {
      console.warn('⚠️ Invalid channel name or user.');
      return;
    }

    console.log(`📡 Creating Channel: ${this.newChannelName} for User ${loggedInUserId}`);

    this.channelService.createChannel(loggedInUserId, this.newChannelName).subscribe({
      next: (newChannel: Channel) => {
        console.log(`✅ Channel Created: ${newChannel.name}`);
        this.fetchChannels(loggedInUserId); // Refresh channel list
        this.closeCreateChannelModal();
      },
      error: (err) => {
        console.error(`❌ Failed to create channel:`, err);
      }
    });
  }  
}
