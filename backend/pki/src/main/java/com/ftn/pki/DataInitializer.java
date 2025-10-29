package com.ftn.pki;

import com.ftn.pki.dto.CertificateRequest;
import com.ftn.pki.model.CertificateTypeEnum;
import com.ftn.pki.service.CertificateService;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

  private final CertificateService certificateService;

  public DataInitializer(CertificateService certificateService) {
    this.certificateService = certificateService;
  }

  @Override
  public void run(String... args) throws Exception {
    // Create ROOT certificate
    CertificateRequest rootRequest = new CertificateRequest();
    rootRequest.setCommonName("Root CA");
    rootRequest.setOrganization("MyOrg");
    rootRequest.setOrganizationalUnit("IT");
    rootRequest.setCountry("US");
    rootRequest.setEmail("root@myorg.com");
    rootRequest.setType(CertificateTypeEnum.ROOT);
    rootRequest.setValidityDays(3650); // 10 years
    rootRequest.setKeyUsages(Arrays.asList("KEYCERTSIGN", "CRLSIGN", "DIGITALSIGNATURE"));

    var rootCert = certificateService.createCertificate(rootRequest);
    System.out.println("Root certificate created: " + rootCert);

    // Create Intermediate certificate issued by Root
    CertificateRequest intermediateRequest = new CertificateRequest();
    intermediateRequest.setCommonName("Intermediate CA");
    intermediateRequest.setOrganization("MyOrg");
    intermediateRequest.setOrganizationalUnit("IT");
    intermediateRequest.setCountry("US");
    intermediateRequest.setEmail("intermediate@myorg.com");
    intermediateRequest.setType(CertificateTypeEnum.INTERMEDIATE);
    intermediateRequest.setIssuerId(rootCert.getId());
    intermediateRequest.setValidityDays(1825); // 5 years
    intermediateRequest.setKeyUsages(Arrays.asList("KEYCERTSIGN", "CRLSIGN", "DIGITALSIGNATURE"));

    var intermediateCert = certificateService.createCertificate(intermediateRequest);
    System.out.println("Intermediate certificate created: " + intermediateCert);

    // Create an End-Entity certificate issued by Intermediate
    CertificateRequest endEntityRequest = new CertificateRequest();
    endEntityRequest.setCommonName("www.example.com");
    endEntityRequest.setOrganization("MyOrg");
    endEntityRequest.setOrganizationalUnit("Web");
    endEntityRequest.setCountry("US");
    endEntityRequest.setEmail("admin@example.com");
    endEntityRequest.setType(CertificateTypeEnum.END_ENTITY);
    endEntityRequest.setIssuerId(intermediateCert.getId());
    endEntityRequest.setValidityDays(365); // 1 year
    endEntityRequest.setKeyUsages(Arrays.asList("DIGITALSIGNATURE", "KEYENCIPHERMENT"));

    var endEntityCert = certificateService.createCertificate(endEntityRequest);
    System.out.println("End-entity certificate created: " + endEntityCert);
  }
}
