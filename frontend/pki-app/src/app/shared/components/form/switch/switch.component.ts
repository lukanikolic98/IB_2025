import { Component, Input, forwardRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-switch',
  standalone: true,
  imports: [CommonModule],
  template: `
    <label class="switch-container" (click)="toggle()">
      <span class="switch-label">{{ label }}</span>
      <div class="slider-container" [class.active]="checked">
        <span class="slider"></span>
      </div>
    </label>
  `,
  styleUrls: ['./switch.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SwitchComponent),
      multi: true,
    },
  ],
})
export class SwitchComponent implements ControlValueAccessor {
  @Input() label = '';
  checked = false;

  private onChange = (val: boolean) => {};
  private onTouched = () => {};

  writeValue(value: boolean): void {
    this.checked = !!value;
  }

  registerOnChange(fn: (val: boolean) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  toggle(): void {
    this.checked = !this.checked;
    this.onChange(this.checked);
    this.onTouched();
  }
}
