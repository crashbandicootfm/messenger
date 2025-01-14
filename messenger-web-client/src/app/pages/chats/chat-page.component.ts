import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {MessageService} from '../../services/message.service';
import {NgClass, NgForOf} from '@angular/common';
import {MessageRequest} from '../../models/request/message-request.model';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-chats-page',
  templateUrl: 'chat-page.component.html',
  styleUrls: ['chat-page.component.css'],
  imports: [FormsModule, NgForOf, NgClass],
  standalone: true,
})
export class ChatPageComponent implements OnInit {
  messages: { user: string; text: string; isEphemeral?: boolean; timerId?: any }[] = [];
  newMessage: string = '';
  chatId: number | null = null;
  showProfileMenu: boolean = false;
  darkMode: boolean = false;
  isEphemeral: boolean = false;
  ephemeralDuration: number = 5000;

  constructor(
    private messageService: MessageService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.chatId = Number(this.route.snapshot.paramMap.get('id'));

    if (this.chatId) {
      console.log(`Chat ID: ${this.chatId}`);
    } else {
      alert('Invalid Chat ID');
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
            user: 'You',
            text: response.message,
            isEphemeral: this.isEphemeral,
          };

          this.messages.unshift(newMsg);

          if (this.isEphemeral) {
            this.scheduleMessageRemoval(newMsg);
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

  scheduleMessageRemoval(message: { user: string; text: string; isEphemeral?: boolean; timerId?: any }): void {
    message.timerId = setTimeout(() => {
      this.messages = this.messages.filter((msg) => msg !== message);
      console.log('Ephemeral message removed:', message.text);
    }, this.ephemeralDuration);
  }

  toggleProfileMenu(): void {
    this.showProfileMenu = !this.showProfileMenu;
  }

  toggleTheme(): void {
    this.darkMode = !this.darkMode;
  }

  viewProfile(): void {
    alert('View Profile functionality coming soon!');
  }

  openSettings(): void {
    alert('Settings functionality coming soon!');
  }

  logout(): void {
    alert('Logging out...');
  }

  logMessage(message: { user: string; text: string }): string {
    console.log(`User: ${message.user}, Message: ${message.text}`);
    return '';
  }
}
