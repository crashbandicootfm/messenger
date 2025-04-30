export interface MessageResponse {
  encrypted?: boolean;
  isEncrypted?: boolean;
  id: number;
  recipientId: number;
  message: string;
  sentAt: string;
  sentDate: string;
  createdBy: number;
  chatId: number;
  fileId?: number;
  fileUrl?: string;
  username: string;
  isRead?: boolean;
}
