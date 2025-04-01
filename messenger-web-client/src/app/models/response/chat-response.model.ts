export interface ChatResponse {
  id: number;
  name: string;
  createdAt: string;
  createdBy?: string;
  userIds: number[];
  lastMessage?: string;
  lastMessageSender?: string;
  unreadCount: number;
}
