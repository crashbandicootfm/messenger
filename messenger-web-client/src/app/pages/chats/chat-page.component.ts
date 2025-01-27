import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {MessageService} from '../../services/message.service';
import {NgClass, NgForOf, NgIf} from '@angular/common';
import {Router} from '@angular/router';
import {MessageRequest} from '../../models/request/message-request.model';
import {ActivatedRoute} from '@angular/router';
import { Subscription, interval } from 'rxjs'
import { v4 as uuidv4 } from 'uuid';

@Component({
  selector: 'app-chats-page',
  templateUrl: 'chat-page.component.html',
  styleUrls: ['chat-page.component.css'],
  imports: [FormsModule, NgForOf, NgClass, NgIf],
  standalone: true,
})
export class ChatPageComponent implements OnInit {
  messages: {
    id: string;
    user: string; text: string; isEphemeral?: boolean; timerId?: any
  } [] = [];

  isProfileMenuOpen: boolean = false;
  newMessage: string = '';
  chatId: number | null = null;
  darkMode: boolean = false;
  isEphemeral: boolean = false;
  ephemeralDuration: number = 5000;
  isSendLaterMenuOpen: boolean = false;
  sendLaterDateTime: string = '';
  menuPosition = { x: 0, y: 0 };
  isMessageTimerMenuOpen: boolean = false;
  ephemeralDurationInput: number = 5;
  chatName: string | null = null;
  private pollingSubscription!: Subscription;

  constructor(
    private router: Router,
    private messageService: MessageService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.chatId = Number(this.route.snapshot.paramMap.get('id'));
    this.chatName = this.route.snapshot.paramMap.get('chatName');

    if (!this.chatId || !this.chatName) {
      alert('Invalid Chat ID or Chat Name');
      this.router.navigate(['/messenger']);
    } else {
      console.log(`Chat ID: ${this.chatId}, Chat Name: ${this.chatName}`);
      this.startPollingMessages();
    }
  }

  ngOnDestroy(): void {
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
    }
  }

  scrollToBottom(): void {
    setTimeout(() => {
      const container = document.querySelector('.chat-messages');
      if (container) {
        container.scrollTop = container.scrollHeight;
      }
    }, 0);
  }

  openMessageTimerMenu(event: MouseEvent): void {
    event.preventDefault();
    this.isMessageTimerMenuOpen = true;
    const button = document.querySelector('.send-button');
    if (button) {
      const rect = button.getBoundingClientRect();
      this.menuPosition = { x: rect.left, y: rect.bottom + 10 };
    }
  }

  closeMessageTimerMenu(): void {
    this.isMessageTimerMenuOpen = false;
  }

  confirmMessageTimer(): void {
    if (this.ephemeralDurationInput > 0) {
      this.ephemeralDuration = this.ephemeralDurationInput * 1000;
      alert(`Messages will auto-delete after ${this.ephemeralDurationInput} seconds.`);
    } else {
      alert('Invalid timer value. Please enter a positive number.');
    }
    this.closeMessageTimerMenu();
  }

  sendMessage(): void {
    if (this.newMessage.trim() && this.chatId) {
      const messageRequest: MessageRequest = {
        message: this.newMessage.trim(),
        chatId: this.chatId,
      };

      this.messageService.sendMessage(messageRequest).subscribe({
        next: (response) => {
          console.log('Message sent successfully:', response);

          const newMsg = {
            id: uuidv4(), // Генерация уникального ID для сообщения
            user: 'You',
            text: response.message,
            isEphemeral: this.isEphemeral,
          };

          this.messages.push(newMsg);

          if (this.isEphemeral && this.ephemeralDuration > 0) {
            this.scheduleMessageRemoval(newMsg.id); // Передача ID сообщения
          }

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

              const newMsg = {
                user: 'You',
                text: response.message,
                isEphemeral: this.isEphemeral,
                id: '',
              };

              this.messages.push(newMsg);

              if (this.isEphemeral) {
                this.scheduleMessageRemoval(newMsg.id);
              }

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

  scheduleMessageRemoval(messageId: string): void {
    setTimeout(() => {
      this.messages = this.messages.filter((msg) => msg.id !== messageId);
      console.log('Ephemeral message removed:', messageId);
    }, this.ephemeralDuration);
  }

  startPollingMessages(): void {
    this.pollingSubscription = interval(1000).subscribe(() => {
      this.fetchMessages();
    });
  }

  fetchMessages(): void {
    if (this.chatId) {
      this.messageService.getMessagesByChatId(this.chatId).subscribe({
        next: (serverMessages) => {
          // Проверяем новые сообщения и добавляем только те, которых ещё нет
          serverMessages.forEach((serverMsg) => {
            if (!this.messages.some((msg) => msg.text === serverMsg.message)) {
              this.messages.push({
                id: uuidv4(), // Присваиваем новый ID
                user: serverMsg.createdBy.toString(),
                text: serverMsg.message,
                isEphemeral: false,
              });
            }
          });
          this.scrollToBottom();
        },
        error: (err) => {
          console.error('Error fetching messages:', err);
        },
      });
    }
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
