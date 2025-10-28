import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-checkbox-group',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './checkbox-group.component.html',
  styleUrls: ['./checkbox-group.component.scss'],
})
export class CheckboxGroupComponent {
  @Input() label = '';
  @Input() options: { label: string; value: string }[] = [];
}
