export interface Message {
  id: number;
  user: string;
  userId?: number | null;
  text: string;
  fileId?: number;
  fileUrl?: string;
  encryptedText?: string | null;
  recipientId?: number;
  isEncrypted?: boolean;
  avatarUrl?: string;
  isEphemeral?: boolean;
  timerId?: any;
  sentAt?: string;
  sentDate?: string;
  isRemoved?: boolean;
  isRead?: boolean;
}
