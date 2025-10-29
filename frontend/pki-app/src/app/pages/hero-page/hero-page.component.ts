import { Component } from '@angular/core';
import { ButtonComponent } from '../../shared/components/button/button.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-hero-page',
  standalone: true,
  imports: [ButtonComponent],
  templateUrl: './hero-page.component.html',
  styleUrl: './hero-page.component.scss',
})
export class HeroPageComponent {
  constructor(private router: Router) {}

  onNavigation(route: string) {
    this.router.navigate([route]);
  }
}
