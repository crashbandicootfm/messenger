import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { JSEncrypt } from 'jsencrypt';
import { firstValueFrom } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class EncryptionService {
  private encryptor = new JSEncrypt({ default_key_size: '2048' });
  private privateKey: string | null = null;
  private publicKey: string | null = null;

  constructor(private http: HttpClient) {
    this.loadKeys();
  }

  private loadKeys(): void {
    // Пытаемся загрузить ключи из localStorage
    this.privateKey = localStorage.getItem('privateKey');

    if (!this.privateKey) {
      this.generateKeys();
    }
  }

  private generateKeys(): void {
    this.encryptor.getKey();
    this.privateKey = this.encryptor.getPrivateKey();
    const publicKey = this.encryptor.getPublicKey();

    // Сохраняем ключи
    localStorage.setItem('privateKey', this.privateKey);
    localStorage.setItem('publicKey', publicKey);

    console.log('New keys generated');
  }

  async encryptForChat(message: string, chatId: number): Promise<string> {
    // Получаем ключи всех участников чата
    const recipientsKeys = await firstValueFrom(
      this.http.get<string[]>(`http://localhost:8080/api/v1/keys/chat-keys/${chatId}`)
    );

    // Для простоты шифруем тем же ключом для всех
    // В реальном приложении нужно шифровать для каждого участника отдельно
    if (recipientsKeys.length === 0) {
      throw new Error('No recipients found');
    }

    return this.encryptMessage(message, recipientsKeys[0]);
  }

  getPrivateKey(): string {
    return this.privateKey!;
  }

  encryptMessage(message: string, recipientPublicKey: string): string {
    const tempEncryptor = new JSEncrypt();
    tempEncryptor.setPublicKey(recipientPublicKey);
    const encrypted = tempEncryptor.encrypt(message);
    if (!encrypted) {
      throw new Error('Encryption failed. Possibly invalid public key or message too long.');
    }
    return encrypted;
  }


  decryptMessage(encryptedMessage: string): string {
    const decryptor = new JSEncrypt();
    decryptor.setPrivateKey(this.privateKey!);
    return <string>decryptor.decrypt(encryptedMessage)!;
  }

  sendPublicKey(clientId: number | null) {
    this.generateKeys();
    return this.http.post('http://localhost:8080/api/v1/keys/store-client-key', {
      clientId: clientId,
      publicKey: this.publicKey
    }).subscribe(response => {
      console.log('Public key stored:', response);
    });
  }

  getRecipientPublicKey(recipientId: number): Promise<string> {
    return firstValueFrom(
      this.http.get(`http://localhost:8080/api/v1/keys/get-client-key/${recipientId}`, {
        responseType: 'text',
      })
    );
  }
}
