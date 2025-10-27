import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-nav-button',
  standalone: true,
  imports: [],
  templateUrl: './nav-button.component.html',
  styleUrl: './nav-button.component.scss',
})
export class NavButtonComponent {
  @Input() label: string = '';
  @Output() clicked = new EventEmitter<void>();
}
