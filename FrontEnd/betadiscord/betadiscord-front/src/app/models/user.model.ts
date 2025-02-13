export interface UserDTO {
    id: number;
    username: string;
    friendIds: number[] | null; // Optional list of friend IDs
  }
  