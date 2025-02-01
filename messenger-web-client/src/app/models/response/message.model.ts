export interface Message {
  id: number;
  user: string;
  text: string;
  avatarUrl?: string;
  isEphemeral?: boolean;
  timerId?: any;
  isRemoved?: boolean;
}
