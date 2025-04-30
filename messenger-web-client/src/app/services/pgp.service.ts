import { Injectable } from '@angular/core';
import * as openpgp from 'openpgp';
import { HttpClient } from '@angular/common/http';
import {Observable, of, from, lastValueFrom} from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import {ChatService} from './chat.service';

@Injectable({ providedIn: 'root' })
export class PgpService {
  private publicKeyCache: Map<number, string> = new Map();
  private privateKeyCache: Map<number, string> = new Map();

  constructor(
    private http: HttpClient,
    private chatService: ChatService
  ) {}

  async initializeKeys(userId: number): Promise<boolean> {
    try {
      // Check if we already have keys in cache
      if (this.privateKeyCache.has(userId) && this.publicKeyCache.has(userId)) {
        return true;
      }

      // Try to load from localStorage
      const privateKey = localStorage.getItem(`pgp_private_${userId}`);
      const publicKey = localStorage.getItem(`pgp_public_${userId}`);
      console.log("My public key:", publicKey);

      if (privateKey && publicKey) {
        this.privateKeyCache.set(userId, privateKey);
        this.publicKeyCache.set(userId, publicKey);
        return true;
      }

      // Generate new keys if none exist
      await this.generateKeyPair(userId);
      return true;
    } catch (err) {
      console.error('Key initialization failed:', err);
      return false;
    }
  }

  async generateKeyPair(userId: number): Promise<void> {
    try {
      const { privateKey, publicKey } = await openpgp.generateKey({
        type: 'rsa',
        rsaBits: 2048,
        userIDs: [{
          name: `User ${userId}`,
          email: `user${userId}@example.com` // Email improves key compatibility
        }],
        passphrase: 'hello', // No passphrase for better debugging
        format: 'armored' // Ensure proper formatting
      });

      // Validate key format
      if (!privateKey.includes('BEGIN PGP PRIVATE KEY') ||
        !publicKey.includes('BEGIN PGP PUBLIC KEY')) {
        throw new Error('Generated keys have invalid format');
      }

      // Store keys
      localStorage.setItem(`pgp_private_${userId}`, privateKey.trim());
      localStorage.setItem(`pgp_public_${userId}`, publicKey.trim());

      // Upload public key
      await this.http.post('http://localhost:8080/api/v1/keys/store-client-key', {
        clientId: userId,
        publicKey: publicKey.trim()
      }).toPromise();

      console.log('New keys generated and stored');
    } catch (err) {
      console.error('Key generation failed:', err);
      throw err;
    }
  }

  private storeKeys(userId: number, privateKey: string, publicKey: string): void {
    // Store in memory
    this.privateKeyCache.set(userId, privateKey);
    this.publicKeyCache.set(userId, publicKey);

    // Persist to localStorage
    localStorage.setItem(`pgp_private_${userId}`, privateKey);
    localStorage.setItem(`pgp_public_${userId}`, publicKey);
  }

  getPrivateKey(userId: number): string | null {
    return this.privateKeyCache.get(userId) ||
      localStorage.getItem(`pgp_private_${userId}`);
  }

  getPublicKey(userId: number): Observable<string> {
    if (this.publicKeyCache.has(userId)) {
      return of(this.publicKeyCache.get(userId)!);
    }

    return this.http.get(
      `http://localhost:8080/api/v1/keys/get-client-key/${userId}`,
      { responseType: 'text' }
    ).pipe(
      tap(publicKey => {
        this.publicKeyCache.set(userId, publicKey);
        localStorage.setItem(`pgp_public_${userId}`, publicKey);
      }),
      catchError(err => {
        console.error('Failed to load public key:', err);
        throw err;
      })
    );
  }

  async decryptMessage(encryptedMessage: string, userId: number): Promise<string> {
    try {
      const privateKey = this.getPrivateKey(userId);
      if (!privateKey) {
        throw new Error('No private key available for user ' + userId);
      }

      const privateKeyObj = await openpgp.readPrivateKey({
        armoredKey: this.ensureKeyFormat(privateKey, 'PRIVATE')
      });

      let decryptedKey = privateKeyObj;
      if (!privateKeyObj.isDecrypted()) {
        decryptedKey = await openpgp.decryptKey({
          privateKey: privateKeyObj,
          passphrase: 'hello' // Пароль должен совпадать с тем, что был при генерации
        });
      }

      const message = await openpgp.readMessage({
        armoredMessage: this.ensureKeyFormat(encryptedMessage, 'MESSAGE')
      });

      const { data: decrypted } = await openpgp.decrypt({
        message,
        decryptionKeys: decryptedKey,
        format: 'utf8'
      });

      return decrypted;
    } catch (err) {
      console.error('Detailed decryption error:', {
        error: err,
        userId: userId,
        privateKeyAvailable: !!this.getPrivateKey(userId)
      });
      throw err;
    }
  }

  private ensureKeyFormat(key: string, type: 'PUBLIC' | 'PRIVATE' | 'MESSAGE'): string {
    if (key.includes(`BEGIN PGP ${type}`)) {
      return key;
    }
    return `-----BEGIN PGP ${type}-----\n${key}\n-----END PGP ${type}-----\n`;
  }

  async encryptMessage(message: string, publicKey: string): Promise<string> {
    try {
      const key = await openpgp.readKey({
        armoredKey: this.ensureKeyFormat(publicKey, 'PUBLIC')
      });

      const encrypted = await openpgp.encrypt({
        message: await openpgp.createMessage({ text: message }),
        encryptionKeys: key
      });

      return encrypted;
    } catch (err) {
      console.error('Encryption failed:', err);
      throw err;
    }
  }

  async encryptGroupMessage(message: string, chatId: number): Promise<string> {
    try {
      const keys: string[] = await lastValueFrom(this.chatService.getChatPublicKeys(chatId));

      if (!keys || keys.length === 0) {
        throw new Error('Нет публичных ключей участников чата');
      }

      console.log('🔐 Публичные ключи участников:', keys);

      const encryptionKeys = await Promise.all(
        keys.map(k => openpgp.readKey({ armoredKey: this.ensureKeyFormat(k, 'PUBLIC') }))
      );

      const encrypted = await openpgp.encrypt({
        message: await openpgp.createMessage({ text: message }),
        encryptionKeys,
        format: 'armored'
      });

      return encrypted;
    } catch (err) {
      console.error('Ошибка при шифровании группового сообщения:', err);
      throw err;
    }
  }
}
