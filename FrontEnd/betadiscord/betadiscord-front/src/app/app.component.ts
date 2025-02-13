import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  constructor(private router: Router) {}

  ngOnInit(): void {
    // Check if session is valid; otherwise, clear it and redirect to login
    if (!localStorage.getItem('userId') || !localStorage.getItem('token')) {
      localStorage.clear();
      this.router.navigate(['/login']); // Redirect to login if not authenticated
    }
  }

  isLoggedIn(): boolean {
    // Check if the user is logged in
    return !!localStorage.getItem('userId') && !!localStorage.getItem('token');
  }

  logout(): void {
    // Clear session and redirect to login
    localStorage.removeItem('userId');
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}
