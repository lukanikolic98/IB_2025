import { Component } from '@angular/core';
import { Certificate } from '../../types/certificate';
import { CommonModule } from '@angular/common';
import { mockCertificates } from '../../mock/certificates';
import { FilterBarComponent } from '../../shared/components/filter-bar/filter-bar.component';

@Component({
  selector: 'app-admin-dashboard-page',
  standalone: true,
  imports: [CommonModule, FilterBarComponent],
  templateUrl: './admin-dashboard-page.component.html',
  styleUrls: ['./admin-dashboard-page.component.scss'],
})
export class AdminDashboardPageComponent {
  certificates: Certificate[] = mockCertificates;
  selectedStatus: 'all' | 'active' | 'revoked' = 'all';

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
}
