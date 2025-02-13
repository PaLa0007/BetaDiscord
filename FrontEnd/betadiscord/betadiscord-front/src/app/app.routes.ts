import { Routes } from '@angular/router';
import { ChannelMessagesComponent } from './components/channel-messages/channel-messages.component';
import { FriendMessagesComponent } from './components/friend-messages/friend-messages.component';

export const appRoutes: Routes = [
  { path: 'channels/:id', component: ChannelMessagesComponent },
  { path: 'friends/:id', component: FriendMessagesComponent },
  { path: '', redirectTo: '/channels', pathMatch: 'full' }
];
