package com.ftn.pki.service;

import com.ftn.pki.dto.CertificateRequest;
import com.ftn.pki.dto.CertificateDto;
import com.ftn.pki.model.Certificate;
import com.ftn.pki.model.CertificateTypeEnum;
import com.ftn.pki.repository.CertificateRepository;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CertificateService {

  private final CertificateRepository certificateRepository;
  private final EncryptionService encryptionService;

  @Autowired
  public CertificateService(CertificateRepository certificateRepository, EncryptionService encryptionService) {
    this.certificateRepository = certificateRepository;
    this.encryptionService = encryptionService;
    Security.addProvider(new BouncyCastleProvider());
  }

  public CertificateDto createCertificate(CertificateRequest request) {
    try {
      KeyPair keyPair = generateKeyPair();
      X509Certificate certificate;
      PrivateKey privateKeyToSave = keyPair.getPrivate();
      Certificate issuerEntity = null;

      if (request.getType() == CertificateTypeEnum.ROOT) {
        // ROOT sertifikat - samopotpisan
        X500Name subjectName = new X500Name("CN=" + request.getCommonName() + ", O=" + request.getOrganization()
            + ", OU=" + request.getOrganizationalUnit() + ", C=" + request.getCountry());
        certificate = generateX509Certificate(request, keyPair, subjectName, keyPair.getPrivate());
      } else {
        // ostali sertifikati - Intermediate i End-Entity
        issuerEntity = certificateRepository.findById(request.getIssuerId())
            .orElseThrow(() -> new RuntimeException("Sertifikat izdavaoca nije pronadjen."));

        if (issuerEntity.isRevoked() || issuerEntity.getEndDate().isBefore(LocalDate.now())) {
          throw new RuntimeException("Sertifikat izdavaoca je opozvan ili istekao.");
        }

        byte[] decryptedPrivateKeyBytes = encryptionService.decrypt(issuerEntity.getPrivateKeyData());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decryptedPrivateKeyBytes);
        PrivateKey issuerPrivateKey = keyFactory.generatePrivate(privateKeySpec);

        X500Name issuerName = new X500Name(
            "CN=" + issuerEntity.getCommonName() + ", O=" + issuerEntity.getOrganization()
                + ", OU=" + issuerEntity.getOrganizationalUnit() + ", C=" + issuerEntity.getCountry());

        certificate = generateX509Certificate(request, keyPair, issuerName, issuerPrivateKey);
      }

      Certificate entity = new Certificate();
      entity.setCommonName(request.getCommonName());
      entity.setOrganization(request.getOrganization());
      entity.setOrganizationalUnit(request.getOrganizationalUnit());
      entity.setCountry(request.getCountry());
      entity.setEmail(request.getEmail());
      entity.setType(request.getType());
      entity.setStartDate(LocalDate.now());
      entity.setEndDate(LocalDate.now().plusDays(request.getValidityDays()));
      entity.setSerialNumber(certificate.getSerialNumber().toString());

      if (request.getIssuerId() != null) {
        entity.setIssuerId(request.getIssuerId());
      }

      entity.setPrivateKeyData(encryptionService.encrypt(privateKeyToSave.getEncoded()));
      entity.setCertificateData(certificate.getEncoded());

      certificateRepository.save(entity);

      return mapToDto(entity);
    } catch (Exception e) {
      throw new RuntimeException("Greska pri kreiranju sertifikata: " + e.getMessage(), e);
    }
  }

  private X509Certificate generateX509Certificate(CertificateRequest request, KeyPair keyPair,
      X500Name issuerName, PrivateKey issuerPrivateKey) throws Exception {
    X500Name subjectName = new X500Name("CN=" + request.getCommonName() + ", O=" + request.getOrganization()
        + ", OU=" + request.getOrganizationalUnit() + ", C=" + request.getCountry());

    BigInteger serial = BigInteger.valueOf(new SecureRandom().nextInt() & 0x7fffffff);
    Date notBefore = new Date();
    Date notAfter = Date.from(LocalDate.now().plusDays(request.getValidityDays())
        .atStartOfDay(ZoneId.systemDefault()).toInstant());

    JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
        issuerName, serial, notBefore, notAfter, subjectName, keyPair.getPublic());

    // Subject Alternative Name
    GeneralName emailName = new GeneralName(GeneralName.rfc822Name, request.getEmail());
    GeneralNames subjectAltNames = new GeneralNames(new GeneralName[] { emailName });
    certBuilder.addExtension(Extension.subjectAlternativeName, false, subjectAltNames);

    // KeyUsage
    int keyUsageMask = 0;
    if (request.getKeyUsages() != null) {
      for (String usage : request.getKeyUsages()) {
        switch (usage.toUpperCase()) {
          case "DIGITALSIGNATURE":
            keyUsageMask |= KeyUsage.digitalSignature;
            break;
          case "KEYENCIPHERMENT":
            keyUsageMask |= KeyUsage.keyEncipherment;
            break;
          case "KEYCERTSIGN":
            keyUsageMask |= KeyUsage.keyCertSign;
            break;
          case "CRLSIGN":
            keyUsageMask |= KeyUsage.cRLSign;
            break;
          case "NONREPUDIATION":
            keyUsageMask |= KeyUsage.nonRepudiation;
            break;
          case "DATAENCIPHERMENT":
            keyUsageMask |= KeyUsage.dataEncipherment;
            break;
          case "KEYAGREEMENT":
            keyUsageMask |= KeyUsage.keyAgreement;
            break;
          case "ENCIPHERONLY":
            keyUsageMask |= KeyUsage.encipherOnly;
            break;
          case "DECIPHERONLY":
            keyUsageMask |= KeyUsage.decipherOnly;
            break;
        }
      }
    }
    if (keyUsageMask > 0) {
      certBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(keyUsageMask));
    }

    // BasicConstraints
    if (request.getType() != CertificateTypeEnum.END_ENTITY) {
      BasicConstraints bc = (request.getPathLength() != null && request.getPathLength() >= 0)
          ? new BasicConstraints(request.getPathLength())
          : new BasicConstraints(true);
      certBuilder.addExtension(Extension.basicConstraints, true, bc);
    } else {
      certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));
    }

    // CRL Distribution Point
    if (request.getIssuerId() != null) {
      String crlUrl = "https://localhost:8443/certificates/" + request.getIssuerId() + "/crl";

      GeneralName crlUri = new GeneralName(GeneralName.uniformResourceIdentifier, crlUrl);
      GeneralNames crlDistributionPoint = new GeneralNames(new GeneralName[] { crlUri });
      DistributionPointName dpn = new DistributionPointName(crlDistributionPoint);
      DistributionPoint dp = new DistributionPoint(dpn, null, null);

      CRLDistPoint crldp = new CRLDistPoint(new DistributionPoint[] { dp });
      certBuilder.addExtension(Extension.cRLDistributionPoints, false, crldp);
    }

    ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSA").setProvider("BC").build(issuerPrivateKey);
    return new JcaX509CertificateConverter().setProvider("BC").getCertificate(certBuilder.build(signer));
  }

  public List<CertificateDto> getAllCertificates() {
    return certificateRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
  }

  public boolean revokeCertificate(Long id, int reasonCode) {
    Optional<Certificate> certOpt = certificateRepository.findById(id);
    if (certOpt.isPresent()) {
      Certificate cert = certOpt.get();
      if (cert.isRevoked())
        return true;

      cert.setRevoked(true);
      cert.setRevocationDate(LocalDate.now());
      cert.setRevocationReason(reasonCode);
      certificateRepository.save(cert);

      if (cert.getType() != CertificateTypeEnum.END_ENTITY) {
        revokeChildrenCertificates(cert.getId(), reasonCode);
      }
      return true;
    }
    return false;
  }

  private void revokeChildrenCertificates(Long issuerId, int reasonCode) {
    List<Certificate> children = certificateRepository.findByIssuerId(issuerId);
    for (Certificate child : children) {
      if (!child.isRevoked()) {
        child.setRevoked(true);
        child.setRevocationDate(LocalDate.now());
        child.setRevocationReason(reasonCode);
        certificateRepository.save(child);

        if (child.getType() != CertificateTypeEnum.END_ENTITY) {
          revokeChildrenCertificates(child.getId(), reasonCode);
        }
      }
    }
  }

  private KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
    keyGen.initialize(2048);
    return keyGen.generateKeyPair();
  }

  private CertificateDto mapToDto(Certificate entity) {
    return new CertificateDto(
        entity.getId(),
        entity.getCommonName(),
        entity.getOrganization(),
        entity.getOrganizationalUnit(),
        entity.getCountry(),
        entity.getEmail(),
        entity.getType(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.isRevoked());
  }

  public boolean isCertificateRevoked(String serialNumber) {
    return certificateRepository.findBySerialNumber(serialNumber)
        .map(Certificate::isRevoked).orElse(false);
  }

  public byte[] downloadCertificate(Long id) {
    return certificateRepository.findById(id)
        .map(Certificate::getCertificateData)
        .orElseThrow(() -> new RuntimeException("Sertifikat nije pronadjen."));
  }

  public byte[] downloadPrivateKey(Long id) {
    Certificate entity = certificateRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Sertifikat nije pronadjen."));
    try {
      return encryptionService.decrypt(entity.getPrivateKeyData());
    } catch (Exception e) {
      throw new RuntimeException("Greska pri desifrovanju privatnog kljuca: " + e.getMessage());
    }
  }

  public boolean isCertificateValid(Long id) {
    Optional<Certificate> certOpt = certificateRepository.findById(id);
    if (certOpt.isEmpty())
      return false;

    Certificate certificate = certOpt.get();
    LocalDate today = LocalDate.now();
    if (today.isBefore(certificate.getStartDate()) || today.isAfter(certificate.getEndDate()))
      return false;
    if (certificate.isRevoked())
      return false;

    return validateCertificateChain(certificate);
  }

  private boolean validateCertificateChain(Certificate certificate) {
    if (certificate.getIssuerId() == null) {
      return "ROOT".equals(certificate.getType().name());
    }
    Optional<Certificate> issuerOpt = certificateRepository.findById(certificate.getIssuerId());
    if (issuerOpt.isEmpty())
      return false;

    Certificate issuer = issuerOpt.get();
    if (issuer.isRevoked())
      return false;

    LocalDate today = LocalDate.now();
    if (today.isBefore(issuer.getStartDate()) || today.isAfter(issuer.getEndDate()))
      return false;

    return validateCertificateChain(issuer);
  }

  public byte[] generatePkcs12Keystore(Long id, String password) throws Exception {
    Certificate entity = certificateRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Sertifikat nije pronađen."));

    byte[] decryptedPrivateKeyBytes = encryptionService.decrypt(entity.getPrivateKeyData());
    PrivateKey privateKey = KeyFactory.getInstance("RSA")
        .generatePrivate(new PKCS8EncodedKeySpec(decryptedPrivateKeyBytes));

    CertificateFactory certFactory = CertificateFactory.getInstance("X.509", "BC");
    X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(
        new ByteArrayInputStream(entity.getCertificateData()));

    List<X509Certificate> chain = new ArrayList<>();
    chain.add(certificate);

    Long currentIssuerId = entity.getIssuerId();
    while (currentIssuerId != null) {
      Certificate issuerEntity = certificateRepository.findById(currentIssuerId)
          .orElseThrow(() -> new RuntimeException("Izdavalac u lancu nije pronađen."));
      X509Certificate issuerCert = (X509Certificate) certFactory.generateCertificate(
          new ByteArrayInputStream(issuerEntity.getCertificateData()));
      chain.add(issuerCert);
      currentIssuerId = issuerEntity.getIssuerId();
    }

    X509Certificate[] chainArray = chain.toArray(new X509Certificate[0]);

    KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
    keyStore.load(null, null);

    String alias = entity.getCommonName().replaceAll("\\s", "_") + "_key";
    char[] keyPassword = password.toCharArray();

    keyStore.setKeyEntry(alias, privateKey, keyPassword, chainArray);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    keyStore.store(bos, keyPassword);
    return bos.toByteArray();
  }
}