package it.marcodemartino.yapga.client.logic.services;

import it.marcodemartino.yapga.client.logic.certificates.CertificateReaderWriter;
import it.marcodemartino.yapga.common.certificates.IdentityCertificate;
import it.marcodemartino.yapga.common.encryption.symmetric.SymmetricEncryption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CertificatesService {

    private final Logger logger = LogManager.getLogger(CertificatesService.class);
    private final CertificateReaderWriter certificateReaderWriter;
    private final SymmetricEncryption symmetricEncryption;
    private IdentityCertificate identityCertificate;

    public CertificatesService(CertificateReaderWriter certificateReaderWriter, SymmetricEncryption symmetricEncryption) {
        this.certificateReaderWriter = certificateReaderWriter;
        this.symmetricEncryption = symmetricEncryption;
    }

    public void readCertificate() {
        logger.info("Reading a certificate");
        this.identityCertificate = certificateReaderWriter.readCertificate(symmetricEncryption);
    }

    public void writeCertificate(IdentityCertificate identityCertificate) {
        this.identityCertificate = identityCertificate;
        logger.info("Writing a certificate");
        certificateReaderWriter.writeCertificate(identityCertificate, symmetricEncryption);
    }

    public boolean doesCertificateExist() {
        return certificateReaderWriter.doesCertificateExist();
    }

    public IdentityCertificate getIdentityCertificate() {
        return identityCertificate;
    }
}
