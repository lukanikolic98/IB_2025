import { Component, Input, forwardRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-checkbox-group',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="checkbox-group">
      <label *ngIf="label" class="group-label">{{ label }}</label>
      <div *ngFor="let option of options" class="checkbox-item">
        <input
          type="checkbox"
          [id]="option.value"
          [checked]="isChecked(option.value)"
          (change)="onCheckboxChange(option.value, $event)"
        />
        <label [for]="option.value">{{ option.label }}</label>
      </div>
    </div>
  `,
  styleUrls: ['./checkbox-group.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CheckboxGroupComponent),
      multi: true,
    },
  ],
})
export class CheckboxGroupComponent implements ControlValueAccessor {
  @Input() label = '';
  @Input() options: { label: string; value: string }[] = [];

  value: string[] = [];

  private onChange = (val: string[]) => {};
  private onTouched = () => {};

  writeValue(value: string[]): void {
    this.value = value ?? [];
  }

  registerOnChange(fn: (val: string[]) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  isChecked(val: string): boolean {
    return this.value.includes(val);
  }

  onCheckboxChange(val: string, event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;

    if (checked && !this.value.includes(val)) {
      this.value = [...this.value, val];
    } else if (!checked) {
      this.value = this.value.filter((v) => v !== val);
    }

    this.onChange(this.value);
    this.onTouched();
  }
}
