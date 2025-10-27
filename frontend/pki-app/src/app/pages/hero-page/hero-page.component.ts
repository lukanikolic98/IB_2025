import { Component } from '@angular/core';
import { ButtonComponent } from '../../shared/components/button/button.component';

@Component({
  selector: 'app-hero-page',
  standalone: true,
  imports: [ButtonComponent],
  templateUrl: './hero-page.component.html',
  styleUrl: './hero-page.component.scss',
})
export class HeroPageComponent {}
