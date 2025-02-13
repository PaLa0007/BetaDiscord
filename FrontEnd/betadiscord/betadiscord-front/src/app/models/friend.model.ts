export interface Friend {
    id: number; // Unique identifier for the friend
    username?: string; // Optional: Friend's username
    friendIds?: number[]; // Optional: List of friend IDs
  }