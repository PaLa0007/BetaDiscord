export interface Message {
  id: number;
  sender: { id: number; username: string }; // ✅ Sender is now always an object
  content: string;
  timestamp: string;
}
