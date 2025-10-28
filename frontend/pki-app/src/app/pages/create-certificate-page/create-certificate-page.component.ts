import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CreateCertificateFormComponent } from '../../shared/components/certificate/create-certificate-form/create-certificate-form.component';

@Component({
  selector: 'app-create-certificate-page',
  standalone: true,
  imports: [CommonModule, CreateCertificateFormComponent],
  templateUrl: './create-certificate-page.component.html',
  styleUrls: ['./create-certificate-page.component.scss'],
})
export class CreateCertificatePageComponent {}
