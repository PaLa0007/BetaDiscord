import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  imports: [HttpClientModule], // Импортира HTTP модула
  providers: []               // Сервизите са вече в `providedIn: 'root'`
})
export class ServicesModule {}
