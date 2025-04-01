export interface TokenResponse {
  twoFactorRequired: boolean;
  token: string;
  refreshToken: string;
  id: number;
}
