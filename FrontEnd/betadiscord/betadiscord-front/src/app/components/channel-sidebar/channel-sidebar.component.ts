import { Component, OnInit, HostListener } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router'; // ✅ Import Router
import { ChannelService } from '../../services/channel.service';
import { ChannelUsersDTO } from '../../models/channel-users.model';
import { UserDTO } from '../../models/user.model';
import { EventEmitter, Output } from '@angular/core'; // Add this to imports


@Component({
  selector: 'app-channel-sidebar',
  templateUrl: './channel-sidebar.component.html',
  styleUrls: ['./channel-sidebar.component.css'],
})
export class ChannelSidebarComponent implements OnInit {
  channelId!: number;
  users: ChannelUsersDTO | null = null;
  contextMenuVisible = false;
  contextMenuPosition = { x: 0, y: 0 };
  selectedUser: { id: number; role: string } | null = null;
  loggedInUserId!: number;
  isOwner = false;
  isAdmin = false;
  isDeleteModalOpen = false; // ✅ Delete Confirmation Modal State
  @Output() channelDeleted = new EventEmitter<number>(); // ✅ Event to notify parent
  channels: any[] = []; // ✅ Declare the array to store channels



  // 🔍 User Search Modal Variables
  isUserSearchModalOpen = false;
  searchQuery = '';
  searchResults: UserDTO[] = [];
  selectedRole: { [userId: number]: string } = {}; // ✅ Role selection for adding users

  constructor(
    private route: ActivatedRoute,
    private router: Router, // ✅ Inject Router
    private channelService: ChannelService
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.channelId = Number(params.get('id'));
      this.loggedInUserId = Number(localStorage.getItem('userId'));

      console.log(`🆔 Right Sidebar - Detected Channel ID: ${this.channelId}`);
      console.log(`👤 Right Sidebar - Logged-in User ID: ${this.loggedInUserId}`);

      if (!this.channelId || isNaN(this.channelId) || this.channelId === 0) {
        console.error('❌ Right Sidebar - Invalid channel ID detected:', this.channelId);
        return;
      }

      this.fetchChannelUsers();
    });
  }

  fetchChannelUsers(): void {
    if (!this.channelId) {
      console.error('❌ Channel ID is missing, cannot fetch users.');
      return;
    }

    this.channelService.getChannelUsers(this.channelId).subscribe({
      next: (data) => {
        this.users = {
          owners: data.owners || [],
          admins: data.admins || [],
          guests: data.guests || []
        };

        this.isOwner = this.users.owners.some(owner => owner.id === this.loggedInUserId);
        this.isAdmin = this.users.admins.some(admin => admin.id === this.loggedInUserId);
      },
      error: (err) => {
        console.error('❌ Error fetching channel users:', err);
      }
    });
  }

  // ✅ Right-Click Handling for User Actions
  handleRightClick(event: MouseEvent, role: string, userId: number): void {
    event.preventDefault();
    this.selectedUser = { id: userId, role };
    this.contextMenuVisible = true;
    this.contextMenuPosition = { x: event.clientX, y: event.clientY };
  }

  @HostListener('document:click')
  closeContextMenu(): void {
    this.contextMenuVisible = false;
  }

  // ✅ Role-Based Actions
  canPromoteToAdmin(userId: number): boolean {
    const isOwner = this.users?.owners.some(owner => owner.id === this.loggedInUserId) || false;
    const isGuest = this.users?.guests.some(guest => guest.id === userId) || false;

    return isOwner && isGuest;
  }

  canDemoteAdmin(userId: number): boolean {
    const isOwner = this.users?.owners.some(owner => owner.id === this.loggedInUserId) || false;
    const isAdmin = this.users?.admins.some(admin => admin.id === userId) || false;

    return isOwner && isAdmin;
  }

  canRemoveUser(userId: number): boolean {
    const isOwner = this.users?.owners.some(owner => owner.id === this.loggedInUserId) || false;
    const isAdmin = this.users?.admins.some(admin => admin.id === this.loggedInUserId) || false;
    const isTargetGuest = this.users?.guests.some(guest => guest.id === userId) || false;

    if (isOwner) return true;
    if (isAdmin) return isTargetGuest;

    return false;
  }

  // ✅ Implement the actions (must be connected to the backend)
  promoteToAdmin(userId: number): void {
    this.channelService.changeUserRole(this.channelId, this.loggedInUserId, userId, "ADMIN").subscribe({
      next: () => this.fetchChannelUsers(),
      error: (err) => console.error(`❌ Failed to promote User ${userId}:`, err)
    });
  }

  demoteAdmin(userId: number): void {
    this.channelService.changeUserRole(this.channelId, this.loggedInUserId, userId, "GUEST").subscribe({
      next: () => this.fetchChannelUsers(),
      error: (err) => console.error(`❌ Failed to demote User ${userId}:`, err)
    });
  }

  removeUser(userId: number): void {
    this.channelService.removeUserFromChannel(this.channelId, this.loggedInUserId, userId).subscribe({
      next: () => this.fetchChannelUsers(),
      error: (err) => console.error(`❌ Failed to remove User ${userId}:`, err)
    });
  }

  // ✅ Open User Search Modal
  openUserSearchModal(): void {
    this.isUserSearchModalOpen = true;
    this.searchQuery = '';
    this.searchResults = [];
  }

  // ✅ Close User Search Modal
  closeUserSearchModal(): void {
    this.isUserSearchModalOpen = false;
  }

  // 🔍 Search Users (Calls backend API)
  searchUsers(): void {
    if (!this.searchQuery.trim()) return;

    this.channelService.searchUsersForChannel(this.searchQuery, this.channelId).subscribe({
      next: (results) => this.searchResults = results,
      error: (err) => console.error(`❌ Failed to search users:`, err)
    });
  }

  // ✅ Add User to Channel
  addUserToChannel(userId: number): void {
    let role = this.isOwner ? (this.selectedRole[userId] || "GUEST") : "GUEST";

    this.channelService.addUserToChannel(this.channelId, this.loggedInUserId, userId, role).subscribe({
      next: () => {
        this.fetchChannelUsers();
        this.closeUserSearchModal();
      },
      error: (err) => console.error(`❌ Failed to add User ${userId}:`, err)
    });
  }

  // ✅ Open Delete Confirmation Modal
  openDeleteConfirmation(): void {
    this.isDeleteModalOpen = true;
  }

  // ❌ Close Delete Confirmation Modal
  closeDeleteConfirmation(): void {
    this.isDeleteModalOpen = false;
  }

  // 🗑️ **Delete Channel**
  deleteChannel(): void {
    if (!this.channelId || !this.loggedInUserId) {
      console.error("❌ Missing channel ID or user ID.");
      return;
    }
  
    console.log(`🗑️ Deleting Channel ${this.channelId} as User ${this.loggedInUserId}`);
  
    this.channelService.deleteChannel(this.channelId, this.loggedInUserId).subscribe({
      next: () => {
        console.log(`✅ Successfully deleted Channel ${this.channelId}`);
  
        // ✅ Emit event to inform parent component
        this.channelDeleted.emit(this.channelId);
  
        // ✅ Fetch updated list from backend
        this.fetchUpdatedChannels();
  
        // ✅ Redirect user if they are currently inside the deleted channel
        this.router.navigate(['/']);
      },
      error: (err) => {
        console.error(`❌ Failed to delete Channel ${this.channelId}:`, err);
      }
    });
  }
  
  fetchUpdatedChannels(): void {
    console.log("🔄 Fetching updated channels...");
    
    this.channelService.getChannelsForUser(this.loggedInUserId).subscribe({
      next: (updatedChannels) => {
        console.log("✅ Updated channels list received:", updatedChannels);
        this.channels = updatedChannels; // Update UI with fresh data
      },
      error: (err) => {
        console.error("❌ Failed to fetch updated channels:", err);
      }
    });
  }
  
}
