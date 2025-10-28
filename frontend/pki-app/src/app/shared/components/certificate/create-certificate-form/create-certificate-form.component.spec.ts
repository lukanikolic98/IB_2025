import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateCertificateFormComponent } from './create-certificate-form.component';

describe('CreateCertificateFormComponent', () => {
  let component: CreateCertificateFormComponent;
  let fixture: ComponentFixture<CreateCertificateFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateCertificateFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CreateCertificateFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
