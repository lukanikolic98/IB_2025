export interface Certificate {
  id: number;
  type: string;
  commonName: string;
  startDate: string;
  endDate: string;
  organization: string;
  organizationalUnit: string;
  country: string;
  email: string;
  revoked: boolean;
}

export interface CertificateRequest {
  type: string;
  commonName: string;
  organization: string;
  organizationalUnit: string;
  country: string;
  email: string;

  validityDays: number;
  keyUsages: string[];
  pathLength?: number;

  issuerId?: number;
}
