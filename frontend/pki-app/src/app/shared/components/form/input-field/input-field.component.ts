// input-field.component.ts
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-input-field',
  standalone: true,
  imports: [CommonModule],
  template: `
    <label [for]="id">{{ label }}</label>
    <input
      [id]="id"
      [type]="type"
      class="form-input"
      [placeholder]="placeholder"
    />
  `,
  styleUrls: ['./input-field.component.scss'],
})
export class InputFieldComponent {
  @Input() label = '';
  @Input() id = '';
  @Input() placeholder = '';
  @Input() type = 'text';
}
