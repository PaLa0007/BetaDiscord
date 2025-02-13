import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ChannelMessagesComponent } from './components/channel-messages/channel-messages.component'; // ✅ Import Channel Messages
import { FriendMessagesComponent } from './components/friend-messages/friend-messages.component'; // ✅ Import Friend Messages
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  { path: 'channels/:id', component: ChannelMessagesComponent }, // ✅ Ensure the route exists
  { path: 'friends/:id', component: FriendMessagesComponent },
  { path: '', redirectTo: '/channels', pathMatch: 'full' }
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
