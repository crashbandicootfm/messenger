import {Component} from '@angular/core';
import {NgClass, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {ChatService} from '../services/chat.service';
import {AuthenticationService} from '../services/authentication.service';
import {HttpClient, HttpHeaders} from '@angular/common/http';

@Component({
  selector: 'app-messenger-page',
  templateUrl: './messenger-page.component.html',
  styleUrls: ['./messenger-page.component.css'],
  imports: [NgIf, FormsModule, NgClass],
  standalone: true
})
export class MessengerPageComponent {
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

  constructor(
    private router: Router,
    private chatService: ChatService,
    private authService: AuthenticationService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.loadUserProfile();
  }

  loadUserProfile(): void {
    this.authService.getUserProfile().subscribe({
      next: (profile) => {
        this.username = profile.username;
        this.userId = profile.id;
        this.avatarUrl = profile.avatarUrl ? `http://localhost:8080${profile.avatarUrl}` : null;

        if (!this.avatarUrl) {
          this.authService.getAvatar(this.userId).subscribe({
            next: (imageBlob) => {
              this.avatarUrl = URL.createObjectURL(imageBlob);
            },
            error: (err) => {
              console.error('Error fetching avatar', err);
            }
          });
        }
      },
      error: (err) => {
        console.error('Error fetching user profile', err);
        alert('Failed to load user profile');
      }
    });
  }

  toggleTheme(): void {
    this.darkMode = !this.darkMode;
    document.body.classList.toggle('dark', this.darkMode);
  }

  toggleProfileMenu(): void {
    this.showProfileMenu = !this.showProfileMenu;
    this.viewingProfile = false;
  }

  createChat(): void {
    const chatName = prompt('Enter the chat name: ');

    if (chatName && chatName.trim()) {
      const token = localStorage.getItem('token');

      console.log('Creating chat with the following details:');
      console.log('Chat Name:', chatName.trim());
      console.log('Token:', token);

      this.chatService.createChat(chatName.trim()).subscribe({
        next: (response) => {
          console.log('Chat created successfully:', response);
          this.chatId = response.id;
          alert(`Chat "${chatName.trim()}" created successfully!`);
          this.router.navigate([`/chats/${this.chatId}`]);
        },
        error: (err) => {
          console.error('Error creating chat', err);
        }
      });
    } else {
      alert('Please enter a valid chat name.');
    }
  }

  joinChat(): void {
    const chatName = prompt('Enter the chat name you want to join: ');

    if (chatName && chatName.trim()) {
      console.log('Attempting to join chat:', chatName.trim());

      this.chatService.joinChat(chatName.trim()).subscribe({
        next: (response) => {
          console.log('Successfully joined chat:', response);
          this.chatId = response.id;
          alert(`Successfully joined chat "${chatName.trim()}"!`);
          this.router.navigate([`/chats/${this.chatId}`]); // Переход в чат
        },
        error: (err) => {
          console.error('Error joining chat:', err);
          alert(`Failed to join chat "${chatName.trim()}". Please try again.`);
        }
      });
    } else {
      alert('Please enter a valid chat name.');
    }
  }

  viewProfile(): void {
    this.viewingProfile = true;
  }

  openSettings(): void {
    alert('Settings functionality coming soon!');
  }

  logout(): void {
    this.router.navigate(['/register']);
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
