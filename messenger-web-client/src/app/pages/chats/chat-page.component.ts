import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {MessageService} from '../../services/message.service';
import {NgClass, NgForOf, NgIf} from '@angular/common';
import {Router} from '@angular/router';
import {MessageRequest} from '../../models/request/message-request.model';
import {ActivatedRoute} from '@angular/router';
import { Subscription, interval } from 'rxjs'
import { NgZone } from '@angular/core';
import {Message} from '../../models/response/message.model';
import {AuthenticationService} from '../../services/authentication.service';
import {ChatService} from '../../services/chat.service';
import {ChatResponse} from '../../models/response/chat-response.model';
import {PgpService} from '../../services/pgp.service';
import * as openpgp from 'openpgp';
import {MessageResponse} from '../../models/response/message-response.model';

@Component({
  selector: 'app-chats-page',
  templateUrl: 'chat-page.component.html',
  styleUrls: ['chat-page.component.css'],
  imports: [FormsModule, NgForOf, NgIf],
  standalone: true,
})
export class ChatPageComponent implements OnInit {
  messages: Message[] = [];
  isProfileMenuOpen: boolean = false;
  currentUserId: number | null = null;
  recipientId: number | null = null;
  recipientPublicKey: string | null = null;
  newMessage: string = '';
  chatId: number | null = null;
  darkMode: boolean = false;
  isEphemeral: boolean = false;
  ephemeralDuration: number = 5000;
  isSendLaterMenuOpen: boolean = false;
  isEphemeralMenuOpen: boolean = false;
  sendLaterDateTime: string = '';
  menuPosition = { x: 0, y: 0 };
  ephemeralDurationInput: number = 5;
  chatName: string | null = null;
  private pollingSubscription!: Subscription;
  username: string | null = null;
  avatarUrl: string | null = null;
  searchQuery: string = '';
  searchActive = false;
  userChats: ChatResponse[] = [];
  selectedChat: ChatResponse | null = null;
  showConfirmation = false;
  chatToLeave: number | null = null;
  file: File | null = null;
  groupPublicKeys: Awaited<string | undefined>[] = [];
  currentUserPublicKey: string | null = null;
  showDeleteConfirm = false;
  messageToDelete: Message | null = null;
  contextMessageMenuVisible = false;
  contextMessageX = 0;
  contextMessageY = 0;
  selectedMessage: Message | null = null;

  constructor(
    private router: Router,
    private messageService: MessageService,
    private authService: AuthenticationService,
    private route: ActivatedRoute,
    private pgpService: PgpService,
    private ngZone: NgZone,
    private chatService: ChatService
  ) {}

  ngOnInit(): void {
    this.chatId = Number(this.route.snapshot.paramMap.get('id'));
    this.chatName = this.route.snapshot.paramMap.get('chatName');

    this.route.queryParams.subscribe(params => {
      this.username = params['username'];
      this.avatarUrl = params['avatarUrl'];
    });

    if (!this.chatId || !this.chatName) {
      alert('Invalid Chat ID or Chat Name');
      this.router.navigate(['/messenger']);
    } else {
      console.log(`Chat ID: ${this.chatId}, Chat Name: ${this.chatName}`);

      // Сначала получаем профиль текущего пользователя
      this.authService.getUserProfile().subscribe(userProfile => {
        this.currentUserId = userProfile.id;
        console.log('Current User ID: ' + this.currentUserId);

        // Инициализируем ключи текущего пользователя
        this.chatService.getChatParticipants(this.chatId!).subscribe({
          next: (participantsCount) => {
            if (!Array.isArray(participantsCount) || participantsCount.length !== 2) {
              this.getParticipantKeysForGroupChat();
            } else {
              this.getRecipientKeyForOneOnOneChat();
            }
          },
          error: (err) => {
            console.error('Error checking participants count:', err);
          }
        });
      });
    }
  }

  getRecipientKeyForOneOnOneChat(): void {
    if (this.chatId === null || this.currentUserId === null) {
      console.warn('Chat ID или текущий пользователь не определены.');
      return;
    }

    this.chatService.getChatParticipants(this.chatId).subscribe({
      next: (participants) => {
        if (!Array.isArray(participants) || participants.length !== 2) {
          console.warn(`Чат не является приватным (1-на-1). Участников: ${participants.length}`);
          return;
        }

        // Получаем ID обоих участников
        const otherParticipant = participants.find(p => p.id !== this.currentUserId);
        if (!otherParticipant) {
          console.error('Не удалось определить другого участника.');
          return;
        }

        // Получаем публичные ключи для обоих участников
        const participantIds = [this.currentUserId, otherParticipant.id];

        // Загружаем публичные ключи для обоих участников
        this.pgpService.getPublicKey(this.currentUserId!).subscribe({
          next: (myPublicKey) => {
            console.log("Мой публичный ключ:", myPublicKey);
            this.currentUserPublicKey = myPublicKey;

            // После загрузки моего ключа загружаем ключ другого участника
            this.pgpService.getPublicKey(otherParticipant.id).subscribe({
              next: (publicKey) => {
                console.log("Публичный ключ получателя:", publicKey);
                this.recipientPublicKey = publicKey;
                this.startPollingMessages(); // Начинаем опрос сообщений, когда оба ключа получены
              },
              error: (err) => {
                console.error("Ошибка получения публичного ключа для получателя:", err);
              }
            });
          },
          error: (err) => {
            console.error("Ошибка получения моего публичного ключа:", err);
          }
        });
      },
      error: (err) => {
        console.error('Ошибка при получении участников чата:', err);
      }
    });
  }

  // getParticipantKeysForGroupChat() {
  //   this.chatService.getParticipantCountByChatName(this.chatName!).subscribe({
  //     next: (participantsCount) => {
  //       if (participantsCount > 2) {
  //         this.getParticipantKeysForGroupChat();
  //         this.startPollingMessages();
  //       } else {
  //         this.getRecipientKeyForOneOnOneChat();
  //         this.startPollingMessages();
  //       }
  //     },
  //     error: (err) => {
  //       console.error('Error checking participants count:', err);
  //       if (err.status === 400 || err.status === 404) {
  //         this.router.navigate(['/messenger']);
  //       }
  //     }
  //   });
  // }

  getParticipantKeysForGroupChat(): void {
    if (!this.chatName || !this.currentUserId) {
      console.error('Имя чата или текущий пользователь не заданы');
      return;
    }

    this.chatService.getParticipantIdsByChatName(this.chatName).subscribe({
      next: async (participantIds: number[]) => {
        try {
          const otherIds = participantIds.filter(id => id !== this.currentUserId);

          const publicKeyPromises = otherIds.map(id =>
            this.pgpService.getPublicKey(id).toPromise()
          );

          const keys = await Promise.all(publicKeyPromises);
          this.groupPublicKeys = keys.filter(k => !!k); // отфильтруем пустые

          console.log('🔐 Публичные ключи участников группы:', this.groupPublicKeys);

          this.startPollingMessages();
        } catch (err) {
          console.error('❌ Ошибка при получении публичных ключей участников:', err);
          alert('Не удалось загрузить ключи участников чата');
        }
      },
      error: (err) => {
        console.error('❌ Не удалось получить участников группы:', err);
        alert('Ошибка при получении участников группы');
        this.router.navigate(['/messenger']);
      }
    });
  }

  ngOnDestroy(): void {
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
    }

    this.messages.forEach((msg) => {
      if (msg.timerId) {
        clearTimeout(msg.timerId);
      }
    });
  }

  onMessageRightClick(event: MouseEvent, message: Message): void {
    event.preventDefault();
    this.contextMessageMenuVisible = true;
    this.contextMessageX = event.clientX;
    this.contextMessageY = event.clientY;
    this.selectedMessage = message;
  }

  deleteSelectedMessage() {
    if (!this.selectedMessage) return;

    this.messageService.deleteMessage(this.selectedMessage.id).subscribe({
      next: () => {
        this.messages = this.messages.filter(m => m.id !== this.selectedMessage?.id);
        this.contextMessageMenuVisible = false;
        this.selectedMessage = null;
      },
      error: (err) => {
        console.error('Ошибка при удалении сообщения', err);
        this.contextMessageMenuVisible = false;
      }
    });
  }

  confirmDeleteMessage(): void {
    if (!this.messageToDelete) return;

    this.messageService.deleteMessage(this.messageToDelete.id).subscribe({
      next: () => {
        // Удаляем сообщение из основного массива
        this.messages = this.messages.filter(
          m => m.id !== this.messageToDelete?.id
        );
        this.messageToDelete = null;
        this.showDeleteConfirm = false;
      },
      error: (err) => {
        console.error("Failed to delete message", err);
        this.showDeleteConfirm = false;
      }
    });
  }

  cancelDelete(): void {
    this.showDeleteConfirm = false;
    this.messageToDelete = null;
  }

  get filteredMessages(): Message[] {
    if (!this.searchQuery) {
      return this.messages;
    }
    return this.messages.filter(message =>
      message.text.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
  }

  toggleSearch() {
    this.searchActive = !this.searchActive;
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.searchActive = false;
  }

  scrollToBottom(): void {
    setTimeout(() => {
      const container = document.querySelector('.chat-messages');
      if (container) {
        container.scrollTop = container.scrollHeight;
      }
    }, 0);
  }

  // openMessageTimerMenu(event: MouseEvent): void {
  //   event.preventDefault();
  //   this.isMessageTimerMenuOpen = true;
  //   const button = document.querySelector('.send-button');
  //   if (button) {
  //     const rect = button.getBoundingClientRect();
  //     this.menuPosition = { x: rect.left, y: rect.bottom + 10 };
  //   }
  // }

  // closeMessageTimerMenu(): void {
  //   this.isMessageTimerMenuOpen = false;
  // }

  // confirmMessageTimer(): void {
  //   if (this.ephemeralDurationInput > 0) {
  //     this.ephemeralDuration = this.ephemeralDurationInput * 1000;
  //     alert(`Messages will auto-delete after ${this.ephemeralDurationInput} seconds.`);
  //   } else {
  //     alert('Invalid timer value. Please enter a positive number.');
  //   }
  //   this.closeMessageTimerMenu();
  // }

  openEphemeralMenu(event: MouseEvent): void {
    event.preventDefault();
    this.isEphemeralMenuOpen = true;
    const button = event.target as HTMLElement;
    const rect = button.getBoundingClientRect();
    this.menuPosition = { x: rect.left, y: rect.bottom + 10 };
  }

  closeEphemeralMenu(): void {
    this.isEphemeralMenuOpen = false;
  }

  confirmEphemeralDuration(): void {
    if (this.ephemeralDurationInput > 0) {
      this.ephemeralDuration = this.ephemeralDurationInput * 1000;
    } else {
      alert('Invalid timer value. Please enter a positive number.');
    }
    this.isEphemeral = true;
    this.dispatchSendMessage();
    this.closeEphemeralMenu();
  }

  async dispatchSendMessage(): Promise<void> {
    if (!this.newMessage.trim() || !this.chatId || !this.currentUserId) return;

    try {
      const participantsCount = (await this.chatService.getChatParticipants(this.chatId!).toPromise()) ?? 0;

      if (!Array.isArray(participantsCount) || participantsCount.length !== 2) {
        this.sendGroupMessage(); // Групповой чат
      } else {
        await this.sendPrivateMessage(); // Приватный чат
      }

    } catch (err) {
      console.error('Ошибка при отправке сообщения:', err);
      alert('Ошибка при отправке сообщения');
    }
  }

  private async sendPrivateMessage(): Promise<void> {
    const originalMessage = this.newMessage.trim();
    let messageToSend = originalMessage;
    let isEncrypted = false;

    let encryptionKeys: string[] = [];

    const recipientPublicKey = this.recipientPublicKey;
    const myPublicKey = await this.pgpService.getPublicKey(this.currentUserId!).toPromise();

    if (recipientPublicKey && myPublicKey) {
      encryptionKeys = [recipientPublicKey, myPublicKey];
    }

    if (encryptionKeys.length > 0) {
      messageToSend = await this.encryptMessage(originalMessage, encryptionKeys);
      isEncrypted = true;
    }

    const messageRequest: MessageRequest = {
      message: messageToSend,
      chatId: this.chatId!,
      isEncrypted: isEncrypted
    };

    this.messageService.sendMessage(messageRequest).subscribe({
      next: (response) => {
        const messageSentSound = new Audio('http://localhost:8080/assets/sound.mp3');
        messageSentSound.play();

        const newMsg: Message = {
          id: response.id,
          user: this.username || 'You',
          text: originalMessage,
          isEphemeral: this.isEphemeral,
          isRemoved: false,
          userId: this.currentUserId,
          isEncrypted: isEncrypted,
          isRead: true,
        };

        if (this.isEphemeral) {
          this.scheduleMessageRemoval(newMsg);
        }

        this.messages.push(newMsg);
        this.newMessage = '';
        this.scrollToBottom();
      },
      error: (err) => {
        console.error('Error sending message:', err);
      }
    });
  }

  private sendGroupMessage(): void {
    if (this.newMessage.trim() && this.chatId) {
      const messageRequest: MessageRequest = {
        message: this.newMessage.trim(),
        chatId: this.chatId,
      };

      this.messageService.sendMessage(messageRequest).subscribe({
        next: (response) => {
          console.log('Message sent successfully:', response);

          const messageSentSound = new Audio('http://localhost:8080/assets/sound.mp3');
          messageSentSound.play();

          const newMsg: Message = {
            id: response.id,
            user: this.username || 'You',
            text: response.message,
            isEphemeral: this.isEphemeral,
            isRemoved: false,
            userId: this.currentUserId ?? undefined,
          };

          if (this.isEphemeral) {
            this.scheduleMessageRemoval(newMsg);
          }

          this.messages.push(newMsg);
          this.newMessage = '';
          this.scrollToBottom();
        },
        error: (err) => {
          console.error('Error sending message:', err);
        },
      });
    } else {
      alert('Message or Chat ID is invalid.');
    }
  }

  handleKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault(); // отменяем перевод строки
      this.dispatchSendMessage();
    }
  }

  private async encryptMessage(message: string, publicKeys: string[]): Promise<string> {
    try {
      // Преобразуем все публичные ключи в OpenPGP объекты
      const encryptionKeys = await Promise.all(
        publicKeys.map(key => openpgp.readKey({ armoredKey: key }))
      );

      // Создаём сообщение и шифруем его для всех получателей
      const encrypted = await openpgp.encrypt({
        message: await openpgp.createMessage({ text: message }),
        encryptionKeys: encryptionKeys
      });

      console.log('Encrypted message:', encrypted);
      return encrypted;
    } catch (err) {
      console.error('Ошибка при шифровании:', err);
      throw err;
    }
  }

  private async decryptMessage(encryptedMessage: string, userId: number): Promise<string> {
    try {
      const privateKey = this.pgpService.getPrivateKey(userId);
      if (!privateKey) {
        console.error(`No private key found for user ${userId}`);
        return '🔒 No decryption key available';
      }

      const privateKeyObj = await openpgp.readPrivateKey({ armoredKey: privateKey });

      const message = await openpgp.readMessage({
        armoredMessage: encryptedMessage.includes('-----BEGIN PGP MESSAGE-----')
          ? encryptedMessage
          : `-----BEGIN PGP MESSAGE-----\n${encryptedMessage}\n-----END PGP MESSAGE-----`
      });


      const { data: decrypted } = await openpgp.decrypt({
        message,
        decryptionKeys: privateKeyObj,
        format: 'utf8'
      });

      return decrypted;
    } catch (err) {
      console.error('Decryption failed:', err);
      return '🔒 Decryption failed';
    }
  }

  async getAllParticipantPublicKeys(): Promise<string[]> {
    try {
      const participants = await this.chatService.getChatParticipants(this.chatId!).toPromise();
      const keys: string[] = [];

      if (participants) {
        for (const participant of participants) {
          const key = await this.pgpService.getPublicKey(participant.id).toPromise();
          if (key) {
            keys.push(key);
          } else {
            console.warn(`Не удалось получить публичный ключ для участника: ${participant.id}`);
          }
        }
      }

      return keys;
    } catch (error) {
      console.error('Ошибка при получении участников чата:', error);
      return [];
    }
  }

  closeSendLaterMenu(): void {
    this.isSendLaterMenuOpen = false;
  }

  scheduleSendMessage(): void {
    if (this.newMessage.trim() && this.chatId && this.sendLaterDateTime) {
      const sendTime = new Date(this.sendLaterDateTime).getTime();
      const currentTime = Date.now();

      if (sendTime > currentTime) {
        const delay = sendTime - currentTime;

        const messageRequest: MessageRequest = {
          message: this.newMessage.trim(),
          chatId: this.chatId,
        };

        setTimeout(() => {
          this.messageService.sendMessage(messageRequest).subscribe({
            next: (response) => {
              console.log('Scheduled message sent successfully:', response);

              const newMsg: Message = {
                id: response.id,
                user: 'You',
                text: response.message,
                isEphemeral: this.isEphemeral,
              };

              this.messages.push(newMsg);

              // if (this.isEphemeral) {
              //   this.scheduleMessageRemoval(newMsg);
              // }

              this.scrollToBottom();
            },
            error: (err) => {
              console.error('Error sending scheduled message:', err);
            },
          });
        }, delay);

        this.newMessage = '';
        this.closeSendLaterMenu();
      } else {
        alert('Selected time must be in the future.');
      }
    } else {
      alert('Invalid message, chat ID, or date/time.');
    }
  }

  scheduleMessageRemoval(message: Message): void {
    message.timerId = setTimeout(() => {
      this.ngZone.run(() => {
        message.isRemoved = true;

        this.messageService.deleteMessage(message.id).subscribe({
          next: () => {
            console.log('Message removed from server:', message.text);

            this.messages = this.messages.filter((msg) => msg.id !== message.id);
            this.isEphemeral = false;
          },
          error: (err) => {
            console.error('Error removing message from server:', err);
          },
        });
      });
    }, this.ephemeralDuration);
  }

  startPollingMessages(): void {
    this.pollingSubscription = interval(1000).subscribe(() => {
      this.dispatchFetchMessages();
    });
  }

  confirmLeaveChat() {
    if (!this.selectedChat && !this.chatId) {
      return;
    }

    this.chatToLeave = this.selectedChat ? this.selectedChat.id : this.chatId;

    if (this.chatToLeave === null) {
      console.error("Ошибка: ID чата не может быть null");
      return;
    }

    this.showConfirmation = true;
  }

  cancelLeaveChat() {
    this.showConfirmation = false;
    this.chatToLeave = null;
  }

  leaveSelectedChat() {
    if (this.chatToLeave === null) {
      return;
    }

    this.chatService.leaveChat(this.chatToLeave).subscribe({
      next: () => {
        this.userChats = this.userChats.filter(c => c.id !== this.chatToLeave);

        this.chatService.getUserChats().subscribe((chats) => {
          this.userChats = chats;
        });

        if (this.chatId === this.chatToLeave) {
          this.router.navigate(['/messenger']);
        }

        this.selectedChat = null;
        this.showConfirmation = false;
      },
      error: (err) => {
        console.error('Ошибка при выходе из чата:', err);
      }
    });
  }

  selectChat(chat: ChatResponse) {
    this.selectedChat = chat;
  }

  async dispatchFetchMessages(): Promise<void> {
    if (!this.chatId || !this.chatName || !this.currentUserId) return;

    try {
      const participantsCount = (await this.chatService.getChatParticipants(this.chatId).toPromise()) ?? 0;

      if (!Array.isArray(participantsCount) || participantsCount.length !== 2) {
        this.fetchGroupMessages();
      } else {
        await this.fetchPrivateMessages();
      }

    } catch (err) {
      // console.error('Ошибка при получении сообщений:', err);
    }
  }

  private fetchGroupMessages(): void {
    if (this.chatId) {
      this.messageService.getMessagesByChatId(this.chatId).subscribe({
        next: (messages) => {
          const deletedIds = new Set(this.messages.filter((msg) => msg.isRemoved).map((msg) => msg.id));
          const newMessages = messages.filter((msg) => !deletedIds.has(msg.id));
          const isNewMessage = newMessages.length > this.messages.length;

          this.messages = newMessages.map((msg) => ({
            id: msg.id,
            user: msg.username,
            text: msg.message,
            isEphemeral: false,
            sentAt: new Date(msg.sentAt).toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' }),
            sentDate: new Date(msg.sentAt).toLocaleDateString('ru-RU'),
            isRemoved: false,
            fileUrl: msg.fileUrl,
            userId: msg.createdBy,
          }));

          if (isNewMessage) {
            const newMessageSound = new Audio('http://localhost:8080/assets/sound1.mp3');
            newMessageSound.play();
          }
        },
        error: (err) => {
          console.error('Error fetching messages:', err);
        },
      });
    }
  }

  private async fetchPrivateMessages(): Promise<void> {
    if (!this.chatId || !this.currentUserId) return;

    const keysReady = await this.pgpService.initializeKeys(this.currentUserId);
    if (!keysReady) {
      console.error('Failed to initialize PGP keys');
      return;
    }

    this.messageService.getMessagesByChatId(this.chatId).subscribe({
      next: async (messages) => {
        const processedMessages = [];

        for (const msg of messages) {
          let text = msg.message;

          if (msg.isEncrypted) {
            try {
              text = await this.pgpService.decryptMessage(msg.message, this.currentUserId!);
            } catch (err) {
              console.error('Decryption error:', err);
              text = '🔒 Не удалось расшифровать сообщение';
            }
          }

          processedMessages.push({
            id: msg.id,
            user: msg.username || 'Неизвестный',
            text,
            isEphemeral: false,
            sentAt: new Date(msg.sentAt).toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' }),
            sentDate: new Date(msg.sentAt).toLocaleDateString('ru-RU'),
            isRemoved: false,
            fileUrl: msg.fileUrl,
            userId: msg.createdBy,
            isEncrypted: msg.isEncrypted,
            recipientId: msg.recipientId
          });
        }

        this.messages = processedMessages;
      },
      error: (err) => {
        console.error('Ошибка при получении сообщений:', err);
      }
    });
  }

  toggleProfileMenu(): void {
    this.isProfileMenuOpen = !this.isProfileMenuOpen;
  }

  toggleSendLaterMenu(): void {
    this.isSendLaterMenuOpen = !this.isSendLaterMenuOpen;

    if (this.isSendLaterMenuOpen) {
      const button = document.querySelector('.send-later-button');
      if (button) {
        const rect = button.getBoundingClientRect();
        this.menuPosition = { x: rect.left, y: rect.bottom + 10 };
      }
    }
  }

  toggleTheme(): void {
    this.darkMode = !this.darkMode;
    document.body.classList.toggle('dark', this.darkMode);
  }

  back(): void {
    this.router.navigate(['/messenger']);
  }


  logMessage(message: { user: string; text: string }): string {
    console.log(`User: ${message.user}, Message: ${message.text}`);
    return '';
  }
}
