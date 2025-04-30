export interface MessageRequest {
    message: string;
    chatId: number | null;
    fileUrl?: string;
    isEncrypted?: boolean;
    recipientId?: number;
}
