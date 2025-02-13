import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms'; // ✅ Import this
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';

import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { LayoutComponent } from './components/layout/layout.component';
import { MessageListComponent } from './components/message-list/message-list.component';
import { FriendMessagesComponent } from './components/friend-messages/friend-messages.component';
import { ChannelMessagesComponent } from './components/channel-messages/channel-messages.component'; // ✅ Ensure this is imported
import { ChannelSidebarComponent } from './components/channel-sidebar/channel-sidebar.component'; // ✅ Import

import { AuthGuard } from './guards/auth.guard';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    SidebarComponent,
    LayoutComponent,
    MessageListComponent,
    FriendMessagesComponent,
    ChannelMessagesComponent, // ✅ Ensure this is declared
    ChannelSidebarComponent // ✅ Ensure it's listed here

  ],
  imports: [
    BrowserModule,
    FormsModule, // ✅ Add FormsModule here
    HttpClientModule,
    RouterModule.forRoot([
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      {
        path: '',
        component: LayoutComponent,
        canActivate: [AuthGuard],
        children: [
          { path: 'home', component: SidebarComponent },
          { path: 'channels/:id', component: ChannelMessagesComponent }, // ✅ Ensure correct path
          { path: 'friends/:id', component: FriendMessagesComponent },
        ],
      },
      { path: '', redirectTo: '/login', pathMatch: 'full' },
    ]),
  ],
  providers: [AuthGuard],
  bootstrap: [AppComponent],
})
export class AppModule {}
