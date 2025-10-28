// create-certificate-form.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InputFieldComponent } from '../../form/input-field/input-field.component';
import { DropdownFieldComponent } from '../../form/dropdown-field/dropdown-field.component';
import { CheckboxGroupComponent } from '../../form/checkbox-group/checkbox-group.component';
import { SwitchComponent } from '../../form/switch/switch.component';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-create-certificate-form',
  standalone: true,
  imports: [
    CommonModule,
    InputFieldComponent,
    DropdownFieldComponent,
    CheckboxGroupComponent,
    SwitchComponent,
    FormsModule,
  ],
  templateUrl: './create-certificate-form.component.html',
  styleUrls: ['./create-certificate-form.component.scss'],
})
export class CreateCertificateFormComponent {
  // Add these properties for your switches
  digitalSignature = false;
  keyEncipherment = false;
}
