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
