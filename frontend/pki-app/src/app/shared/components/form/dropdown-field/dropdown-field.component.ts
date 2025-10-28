// dropdown-field.component.ts
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dropdown-field',
  standalone: true,
  imports: [CommonModule],
  template: `
    <label [for]="id">{{ label }}</label>
    <select [id]="id" class="form-input">
      <option *ngFor="let option of options" [value]="option.value">
        {{ option.label }}
      </option>
    </select>
  `,
  styleUrls: ['./dropdown-field.component.scss'],
})
export class DropdownFieldComponent {
  @Input() label = '';
  @Input() id = '';
  @Input() options: { label: string; value: string }[] = [];
}
