export interface ChannelUsersDTO {
    owners: { id: number; username: string }[];
    admins: { id: number; username: string }[];
    guests: { id: number; username: string }[];
  }
  