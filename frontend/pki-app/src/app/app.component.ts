import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { HeroPageComponent } from './pages/hero-page/hero-page.component';
import { NavbarComponent } from './shared/components/navbar/navbar.component';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  title = 'pki-app';
  showNavbar = true;

  // routes where we want to hide the navbar
  hiddenNavbarRoutes = ['/', '/login', '/register'];

  constructor(private router: Router) {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        const currentUrl = event.urlAfterRedirects || event.url;
        this.showNavbar = !this.hiddenNavbarRoutes.includes(currentUrl);
      });
  }
}
