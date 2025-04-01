import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  HostListener,
  OnInit,
  ViewChild
} from '@angular/core';
import {NgClass, NgForOf, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {ChatService} from '../services/chat.service';
import {AuthenticationService} from '../services/authentication.service';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {ChatResponse} from '../models/response/chat-response.model';
import {animate, style, transition, trigger} from '@angular/animations';
import {UserResponse} from '../models/response/user-response.model';
import {MessageService} from '../services/message.service';

@Component({
  selector: 'app-messenger-page',
  templateUrl: './messenger-page.component.html',
  styleUrls: ['./messenger-page.component.css'],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.Default,
  imports: [NgIf, FormsModule, NgClass, NgForOf],
  animations: [
    trigger('fadeInScale', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.8) translateY(-10px)' }),
        animate('250ms ease-out', style({ opacity: 1, transform: 'scale(1) translateY(0)' }))
      ]),
      transition(':leave', [
        animate('200ms ease-in', style({ opacity: 0, transform: 'scale(0.9) translateY(-10px)' }))
      ])
    ])
  ]
})
export class MessengerPageComponent implements OnInit, AfterViewInit {
  showProfileMenu: boolean = false;
  darkMode: boolean = false;
  chatId: number | null = null;
  username: string | null = null;
  userId: number | null = null;
  avatarUrl: string | null = null;
  showProfile: boolean = false;
  viewingProfile: boolean = false;
  isChangingAvatar: boolean = false;
  selectedFile: File | null = null;
  userChats: ChatResponse[] = [];
  showChatMenu: boolean = false;
  searchQuery: string = '';
  searchResults: UserResponse[] = [];
  searching: boolean = false;
  contextMenuVisible = false;
  contextMenuX = 0;
  contextMenuY = 0;
  selectedChat: any = null;
  messages: any[] = [];
  isSearching: boolean = false;
  showTwoFactorAuth: boolean = false;
  twoFactorCode: number | string = '';
  qrCodeUrl: string = '';
  showEmailModal: boolean = false;
  newEmail: string = '';
  isEmailInputFocused: boolean = false;
  errorMessage: string = "";
  showCreateChatModal = false;
  showPasswordChatModal = false;
  showJoinPasswordChatModal = false;
  showJoinChatModal = false;
  newChatName = '';
  modalStep: number = 1;
  newPassword: string = '';

  constructor(
    private router: Router,
    private chatService: ChatService,
    private messageService: MessageService,
    private authService: AuthenticationService,
    private route: ActivatedRoute,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.loadUserProfile();
    this.loadUserChats();
    this.chatId = +this.route.snapshot.paramMap.get('chatId')!;

    const savedTheme = localStorage.getItem('darkMode');
    this.darkMode = savedTheme ? JSON.parse(savedTheme) : false;
    document.body.classList.toggle('dark', this.darkMode);

    this.chatService.getMessagesByChatId(this.chatId).subscribe(
      (response) => {
        this.messages = response;
        console.log('Messages:', this.messages);
      },
      (error) => {
        console.error('Error fetching messages:', error);
      }
    );
  }

  @ViewChild('chatList') chatList!: ElementRef;

  ngAfterViewInit() {
    setTimeout(() => this.scrollToBottom(), 100);
  }

  scrollToBottom(): void {
    if (this.chatList) {
      this.chatList.nativeElement.scrollTop = this.chatList.nativeElement.scrollHeight;
    }
  }


  loadUserProfile(): void {
    this.authService.getUserProfile().subscribe({
      next: (profile) => {
        this.username = profile.username;
        this.userId = profile.id;

        if (profile.avatarUrl) {
          this.authService.getAvatar(this.userId).subscribe({
            next: (imageBlob) => {
              if (imageBlob.size > 0 && imageBlob.type.startsWith('image/')) {
                this.avatarUrl = URL.createObjectURL(imageBlob);
              } else {
                console.error('Получен некорректный аватар');
                this.avatarUrl = 'http://localhost:8080/assets/avatar.png';
              }
            },
            error: (err) => {
              console.error('Ошибка загрузки аватара', err);
              this.avatarUrl = 'http://localhost:8080/assets/avatar.png';
            }
          });
        } else {
          this.avatarUrl = 'http://localhost:8080/assets/avatar.png';
        }
      },
      error: (err) => {
        console.error('Error fetching user profile', err);
        this.avatarUrl = 'http://localhost:8080/assets/avatar.png';
      }
    });
  }

  closeModal(): void {
    this.showTwoFactorAuth = false;
  }

  loadUserChats(): void {
    this.chatService.getUserChats().subscribe({
      next: (chats) => {
        this.userChats = chats;
        this.updateUnreadCounts();

        // Принудительное обновление Angular Change Detection
        setTimeout(() => {
          this.userChats = [...this.userChats];
        });
      },
      error: (err) => {
        console.error('Ошибка загрузки чатов:', err);
      }
    });
  }

  updateUnreadCounts(): void {
    this.userChats.forEach(chat => {
      if (this.userId) {
        this.chatService.getUnreadMessagesCount(chat.id, this.userId).subscribe({
          next: (unreadCount) => {
            console.log(`Chat ID ${chat.name} unread count:`, unreadCount);
            chat.unreadCount = unreadCount;
          },
          error: (err) => {
            console.error('Ошибка получения непрочитанных сообщений:', err);
          }
        });
      }
    });
  }

  openChat(chat: ChatResponse): void {
    this.router.navigate([`/chats/${chat.id}/${chat.name}`], {
      queryParams: {
        username: this.username,
        avatarUrl: this.avatarUrl
      }
    });

    if (this.userId) {
      this.chatService.markChatAsRead(chat.id, this.userId).subscribe({
        next: () => {
          chat.unreadCount = 0;
        },
        error: (err) => {
          console.error('Ошибка при отметке сообщений как прочитанных:', err);
        }
      });
    }
  }

  searchUsers(): void {
    this.searching = true;
    this.isSearching = true;
    this.authService.searchUsersByName(this.searchQuery.trim()).subscribe({
      next: (users) => {
        this.searchResults = users;
        this.searching = false;
      },
      error: (err) => {
        console.error('Ошибка поиска пользователей:', err);
        alert('Ошибка поиска пользователей');
        this.searching = false;
      }
    });
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.searchResults = [];
    this.isSearching = false;
  }

  startChatWithUser(user: UserResponse): void {
    if (!user.id) return;

    this.chatService.createPrivateChat(user.id).subscribe({
      next: (chat) => {
        console.log('Чат создан:', chat);

        this.chatService.getUserChats().subscribe((chats) => {
          this.userChats = chats;
        });

        this.router.navigate([`/chats/${chat.id}/${user.username}`]);
      },
      error: (err) => {
        console.error('Ошибка создания чата:', err);
        alert('Ошибка создания чата');
      }
    });
  }

  onRightClick(event: MouseEvent, chat: any) {
    event.preventDefault();

    this.selectedChat = chat;
    this.contextMenuX = event.clientX;
    this.contextMenuY = event.clientY;
    this.contextMenuVisible = true;
  }

  @HostListener('document:click')
  closeContextMenu() {
    this.contextMenuVisible = false;
  }

  deleteChat(chat: any) {
    this.chatService.deleteChat(chat.id).subscribe({
      next: () => {
        this.userChats = this.userChats.filter(c => c.id !== chat.id);
        this.contextMenuVisible = false;
        this.chatService.getUserChats().subscribe((chats) => {
          this.userChats = chats;
        });
      },
      error: err => console.error('Ошибка при удалении чата', err)

    });
  }

  toggleTheme(): void {
    this.darkMode = !this.darkMode;
    document.body.classList.toggle('dark', this.darkMode);
    localStorage.setItem('darkMode', JSON.stringify(this.darkMode));
  }

  toggleProfileMenu(): void {
    this.showProfileMenu = !this.showProfileMenu;
    this.viewingProfile = false;
  }

  openChatModal() {
    this.showCreateChatModal = true;
  }

  openJoinChatModal() {
    this.showJoinChatModal = true;
  }

  closeChatModal() {
    this.showCreateChatModal = false;
    this.showJoinChatModal = false;
    this.showPasswordChatModal = false;
    this.showJoinPasswordChatModal = false;
  }

  createChat(): void {
    if (this.newChatName && this.newChatName.trim()) {
      this.chatService.createChat(this.newChatName.trim()).subscribe({
        next: (newChat) => {
          console.log('Chat created successfully:', newChat);

          if (Array.isArray(newChat)) {
            console.error('Expected ChatResponse but received an array', newChat);
            return;
          }

          // Добавляем чат вручную в список
          this.userChats = [...this.userChats, newChat];

          this.router.navigate([`/chats/${newChat.id}/${newChat.name}`], {
            queryParams: { username: this.username, avatarUrl: this.avatarUrl }
          });

          this.closeChatModal();
        },
        error: (err) => console.error('Error creating chat:', err)
      });
    } else {
      alert('Please enter a valid chat name.');
    }
  }

  joinChat(): void {
    const chatName = this.newChatName?.trim();

    if (chatName) {
      console.log('Attempting to join chat:', chatName);

      this.chatService.joinChat(chatName).subscribe({
        next: (response) => {
          console.log('Successfully joined chat:', response);
          this.chatId = response.id;
          alert(`Successfully joined chat "${chatName}"!`);
          this.router.navigate([`/chats/${this.chatId}/${chatName}`], {
            queryParams: {
              username: this.username,
              avatarUrl: this.avatarUrl
            }
          });
          this.closeChatModal();
        },
        error: (err) => {
          console.error('Error joining chat:', err);
          const errorMessage = err.error?.message || 'Unknown error occurred';
          alert(`Failed to join chat "${chatName}". ${errorMessage}`);
        },
      });
    } else {
      alert('Please enter a valid chat name.');
    }
  }

  createChatWithPassword(): void {
    this.showPasswordChatModal = true;
    this.modalStep = 1;
  }

  joinChatWithPassword(): void {
    this.showJoinPasswordChatModal = true;
    this.modalStep = 1;
  }

  nextStep(): void {
    if (this.newChatName?.trim()) {
      this.modalStep = 2;
    } else {
      alert('Please enter a valid chat name.');
    }
  }

  createChatWithPasswordSubmit(): void {
    const chatName = this.newChatName?.trim();
    const password = this.newPassword?.trim();

    if (chatName && password) {
      this.chatService.createChatWithPassword(chatName, password).subscribe({
        next: (response) => {
          console.log('Chat with password created successfully:', response);
          this.chatId = response.id;
          this.router.navigate([`/chats/${this.chatId}/${chatName}`], {
            queryParams: {
              username: this.username,
              avatarUrl: this.avatarUrl
            }
          });
          this.closeChatModal();
        },
        error: (err) => {
          console.error('Error creating chat with password:', err);
          alert('Error creating chat with password.');
        },
      });
    } else {
      alert('Please enter both chat name and password.');
    }
  }

  joinChatWithPasswordSubmit(): void {
    const chatName = this.newChatName?.trim();
    const password = this.newPassword?.trim();

    if (chatName && chatName.trim() && password && password.trim()) {
      console.log('Attempting to join chat with password:', chatName.trim());

      this.chatService.joinChatWithPassword(chatName.trim(), password.trim()).subscribe({
        next: (response) => {
          console.log('Successfully joined chat with password:', response);
          this.chatId = response.id;
          alert(`Successfully joined chat "${chatName.trim()}"!`);
          this.router.navigate([`/chats/${this.chatId}/${chatName.trim()}`], {
            queryParams: {
              username: this.username,
              avatarUrl: this.avatarUrl
            }
          });
          this.closeChatModal();
        },
        error: (err) => {
          console.error('Error joining chat with password:', err);
          const errorMessage = err.error?.message || 'Unknown error occurred';
          alert(`Failed to join chat "${chatName.trim()}". ${errorMessage}`);
        },
      });
    } else {
      alert('Please enter a valid chat name and password.');
    }
  }

  viewProfile(): void {
    this.viewingProfile = true;
  }

  toggleChatMenu(): void {
    this.showChatMenu = !this.showChatMenu;
  }

  openSettings(): void {
    this.showTwoFactorAuth = true;
  }

  addEmail(): void {
    this.showEmailModal = true;
  }

  closeEmailModal(): void {
    this.showEmailModal = false;
  }

  onInputFocus(): void {
    this.isEmailInputFocused = true;
  }

  onInputBlur(): void {
    if (!this.newEmail) {
      this.isEmailInputFocused = false;
    }
  }

  validateEmail(email: string): boolean {
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailPattern.test(email);
  }

  saveEmail(): void {
    if (this.newEmail && this.newEmail.trim()) {
      if (this.validateEmail(this.newEmail.trim())) {
        this.authService.updateEmail(this.newEmail.trim()).subscribe({
          next: () => {
            alert("Email successfully updated!");
            this.closeEmailModal();
          },
          error: (err) => {
            console.error("Error updating email:", err);
            alert("Error updating email.");
          }
        });
      } else {
        alert("Please enter a valid email address.");
      }
    } else {
      alert("Please enter an email.");
    }
  }

  enableTwoFactorAuth(): void {
    if (!this.newEmail || !this.validateEmail(this.newEmail.trim())) {
      this.errorMessage = "Add email first";
      return;
    }

    this.errorMessage = "";

    this.authService.enableTwoFactorAuth().subscribe({
      next: (qrCodeUrl) => {
        this.qrCodeUrl = qrCodeUrl;
        console.log("QR: ", qrCodeUrl);
      },
      error: (err) => {
        console.error("Error: ", err);
        alert("Error turning on two factor authentication.");
      }
    });
  }

  logout(): void {
    localStorage.removeItem('token');
    sessionStorage.removeItem('token');

    this.router.navigate(['/register']).then(() => {
      window.history.replaceState(null, '', '/register');
    });
  }

  changeAvatar(): void {
    this.isChangingAvatar = true;
  }

  backToMainMenu(): void {
    this.viewingProfile = false;
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input?.files?.length) {
      this.selectedFile = input.files[0];
    }
  }

  uploadAvatar(): void {
    if (!this.selectedFile) {
      alert('Please select a file first!');
      return;
    }

    const formData = new FormData();
    formData.append('file', this.selectedFile);

    const token = localStorage.getItem('token');
    if (token) {
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      this.http.post(`http://localhost:8080/api/v1/users/upload-avatar/${this.userId}`, formData, { headers })
        .subscribe({
          next: (response: any) => {
            alert('Avatar uploaded successfully!');
            this.avatarUrl = response.avatarUrl;
            this.isChangingAvatar = false;
          },
          error: (err) => {
            console.error('Error uploading avatar', err);
            alert('Failed to upload avatar');
          }
        });
    } else {
      alert('User not logged in');
    }
  }

}
