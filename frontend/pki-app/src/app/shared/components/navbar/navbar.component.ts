import { Component } from '@angular/core';
import { NavButtonComponent } from './nav-button/nav-button.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [NavButtonComponent],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
})
export class NavbarComponent {
  constructor(private router: Router) {}

  onNavigation(route: string) {
    this.router.navigate([route]);
  }
}
