import { Component, Input, forwardRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-dropdown-field',
  standalone: true,
  imports: [CommonModule],
  template: `
    <label *ngIf="label" [for]="id">{{ label }}</label>
    <select
      [id]="id"
      class="form-input"
      [value]="value"
      (change)="onSelect($event)"
      (blur)="onTouched()"
    >
      <option value="" disabled selected hidden>-- Select --</option>
      <option *ngFor="let option of options" [value]="option.value">
        {{ option.label }}
      </option>
    </select>
  `,
  styleUrls: ['./dropdown-field.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DropdownFieldComponent),
      multi: true,
    },
  ],
})
export class DropdownFieldComponent implements ControlValueAccessor {
  @Input() label = '';
  @Input() id = '';
  @Input() options: { label: string; value: string }[] = [];

  value: string = '';

  private onChange = (val: string) => {};
  protected onTouched = () => {};

  writeValue(value: string): void {
    this.value = value ?? '';
  }

  registerOnChange(fn: (val: string) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  onSelect(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.value = target.value;
    this.onChange(this.value);
  }
}
