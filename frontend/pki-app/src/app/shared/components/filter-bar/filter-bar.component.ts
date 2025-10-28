import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-filter-bar',
  templateUrl: './filter-bar.component.html',
  styleUrls: ['./filter-bar.component.scss'],
  standalone: true,
})
export class FilterBarComponent {
  @Input() selectedStatus: 'all' | 'active' | 'revoked' = 'all';
  @Output() statusChange = new EventEmitter<'all' | 'active' | 'revoked'>();

  changeStatus(status: 'all' | 'active' | 'revoked') {
    this.selectedStatus = status;
    this.statusChange.emit(status);
  }
}
