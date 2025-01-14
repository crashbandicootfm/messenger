export interface ChatResponse {
  id: number;
  name: string;
  createdAt: string;
  createdBy?: string;
  userIds: number[];
}
