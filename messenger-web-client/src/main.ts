import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter, Routes } from '@angular/router';
import {HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import { importProvidersFrom } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import {AuthInterceptor} from './app/interceptors/auth.interceptor';
import {LoginComponent} from './app/login/login.component';
import {RegisterComponent} from './app/register/register.component';
import {AppComponent} from './app/app.component';
import {MessengerPageComponent} from './app/pages/messenger-page.component';
import {AuthGuard} from './app/auth/authguard';
import {ChatPageComponent} from './app/pages/chats/chat-page.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'messenger', component: MessengerPageComponent, canActivate: [AuthGuard] },
  { path: 'chats/:id', component: ChatPageComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
];

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi()),
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    importProvidersFrom(ReactiveFormsModule),
  ],
}).catch(err => console.error(err));
