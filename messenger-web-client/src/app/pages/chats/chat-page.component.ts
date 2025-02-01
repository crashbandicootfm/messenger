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

  constructor(
    private router: Router,
    private messageService: MessageService,
    private route: ActivatedRoute,
    private ngZone: NgZone
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
      this.startPollingMessages();
    }
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
      alert(`Messages will auto-delete after ${this.ephemeralDurationInput} seconds.`);
    } else {
      alert('Invalid timer value. Please enter a positive number.');
    }
    this.isEphemeral = true;
    this.closeEphemeralMenu();
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

          const newMsg: Message = {
            id: response.id,
            user: 'You',
            text: response.message,
            isEphemeral: this.isEphemeral,
            isRemoved: false,
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
      this.fetchMessages();
    });
  }

  fetchMessages(): void {
    if (this.chatId) {
      this.messageService.getMessagesByChatId(this.chatId).subscribe({
        next: (messages) => {
          const deletedIds = new Set(this.messages.filter((msg) => msg.isRemoved).map((msg) => msg.id));

          this.messages = messages
            .filter((msg) => !deletedIds.has(msg.id))
            .map((msg) => ({
              id: msg.id,
              user: msg.createdBy.toString(),
              text: msg.message,
              isEphemeral: false,
            }));

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
