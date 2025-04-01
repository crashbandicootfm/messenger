export interface MessageRequest {
    message: string;
    chatId: number | null;
    fileUrl?: string;
}
