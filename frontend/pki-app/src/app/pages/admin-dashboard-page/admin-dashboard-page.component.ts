import { Component, OnInit } from '@angular/core';
import { Certificate } from '../../types/certificate';
import { CommonModule } from '@angular/common';
import { FilterBarComponent } from '../../shared/components/filter-bar/filter-bar.component';
import { CertificateService } from '../../shared/services/certificate.service';

@Component({
  selector: 'app-admin-dashboard-page',
  standalone: true,
  imports: [CommonModule, FilterBarComponent],
  templateUrl: './admin-dashboard-page.component.html',
  styleUrls: ['./admin-dashboard-page.component.scss'],
})
export class AdminDashboardPageComponent implements OnInit {
  certificates: Certificate[] = [];
  selectedStatus: 'all' | 'active' | 'revoked' = 'all';
  loading = false;

  constructor(private certificateService: CertificateService) {}

  ngOnInit() {
    this.loadCertificates();
  }

  loadCertificates() {
    this.loading = true;
    this.certificateService.getAllCertificates().subscribe({
      next: (certs) => {
        this.certificates = certs;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading certificates', err);
        this.loading = false;
      },
    });
  }

  get filteredCertificates(): Certificate[] {
    if (this.selectedStatus === 'all') return this.certificates;
    if (this.selectedStatus === 'active')
      return this.certificates.filter((c) => !c.revoked);
    if (this.selectedStatus === 'revoked')
      return this.certificates.filter((c) => c.revoked);
    return this.certificates;
  }

  setStatusFilter(status: 'all' | 'active' | 'revoked') {
    this.selectedStatus = status;
  }

  validateCertificate(cert: Certificate) {
    this.certificateService.isCertificateValid(cert.id).subscribe({
      next: (isValid) => {
        alert(`Certificate ${cert.id} is ${isValid ? 'valid' : 'invalid'}`);
      },
      error: (err) => console.error(err),
    });
  }

  revokeCertificate(cert: Certificate) {
    const reasonCode = 0; // or prompt user for reason
    this.certificateService.revokeCertificate(cert.id, reasonCode).subscribe({
      next: (msg) => {
        alert(msg);
        this.loadCertificates();
      },
      error: (err) => console.error(err),
    });
  }

  downloadCertificate(cert: Certificate) {
    this.certificateService
      .downloadKeystore(cert.id, 'your-password-here')
      .subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = `certificate_${cert.id}.p12`;
          a.click();
          window.URL.revokeObjectURL(url);
        },
        error: (err) => console.error(err),
      });
  }
}
