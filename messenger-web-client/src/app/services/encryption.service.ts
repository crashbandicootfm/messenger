import { Injectable } from '@angular/core';
import NodeRSA from 'node-rsa';

@Injectable({
  providedIn: 'root',
})
export class EncryptionService {
  private keyPair: NodeRSA;

  constructor() {
    this.keyPair = new NodeRSA({ b: 2048 });
  }

  generateKeys(): { publicKey: string; privateKey: string } {
    const publicKey = this.keyPair.exportKey('public');
    const privateKey = this.keyPair.exportKey('private');
    return { publicKey, privateKey };
  }

  encryptMessage(message: string, publicKey: string): string {
    const rsa = new NodeRSA(publicKey);
    return rsa.encrypt(message, 'base64');
  }

  decryptMessage(encryptedMessage: string, privateKey: string): string {
    const rsa = new NodeRSA(privateKey);
    return rsa.decrypt(encryptedMessage, 'utf8');
  }
}
