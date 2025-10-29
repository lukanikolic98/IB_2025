import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InputFieldComponent } from '../../form/input-field/input-field.component';
import { DropdownFieldComponent } from '../../form/dropdown-field/dropdown-field.component';
import { CheckboxGroupComponent } from '../../form/checkbox-group/checkbox-group.component';
import { SwitchComponent } from '../../form/switch/switch.component';
import { CertificateService } from '../../../services/certificate.service';
import { Certificate, CertificateRequest } from '../../../../types/certificate';

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
  formData: any = {
    type: '',
    commonName: '',
    organization: '',
    organizationalUnit: '',
    country: '',
    email: '',
    validityDays: 365,
    issuerId: null,
    digitalSignature: false,
    keyEncipherment: false,
  };

  constructor(private certificateService: CertificateService) {}

  onSubmit(): void {
    const request: CertificateRequest = {
      type: this.formData.type,
      commonName: this.formData.commonName,
      organization: this.formData.organization,
      organizationalUnit: this.formData.organizationalUnit,
      country: this.formData.country,
      email: this.formData.email,
      validityDays: this.formData.validityDays,
      keyUsages: this.collectKeyUsages(),
      issuerId: this.formData.issuerId
        ? Number(this.formData.issuerId)
        : undefined,
    };

    console.log('Submitting certificate request:', request);

    this.certificateService.createCertificate(request).subscribe({
      next: (response: Certificate) => {
        console.log('Certificate created successfully:', response);
        alert('Certificate created successfully!');
      },
      error: (err) => {
        console.error('Error creating certificate:', err);
        alert('Error creating certificate!');
      },
    });
  }

  private collectKeyUsages(): string[] {
    const usages: string[] = [];
    if (this.formData.digitalSignature) usages.push('digital_signature');
    if (this.formData.keyEncipherment) usages.push('key_encipherment');
    return usages;
  }
}
