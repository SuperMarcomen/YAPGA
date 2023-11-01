package it.marcodemartino.yapga.client.logic.certificates;

import it.marcodemartino.yapga.client.logic.results.ResultBroadcaster;
import it.marcodemartino.yapga.client.logic.services.CertificatesService;
import it.marcodemartino.yapga.common.certificates.IdentityCertificate;
import it.marcodemartino.yapga.common.encryption.asymmetric.*;
import it.marcodemartino.yapga.common.encryption.asymmetric.rsa.RSAEncryption;
import it.marcodemartino.yapga.common.encryption.symmetric.SymmetricEncryption;
import it.marcodemartino.yapga.common.encryption.symmetric.aes.AESEncryption;
import it.marcodemartino.yapga.common.entities.User;
import it.marcodemartino.yapga.common.services.EncryptionService;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.UUID;
class CertificateFileReaderWriterTest {

    @Test
    void readCertificate() {
        AsymmetricEncryption localSignature = new RSAEncryption(2048);
        AsymmetricEncryption remoteEncryption = new RSAEncryption(2048);
        SymmetricEncryption localEncryption = new AESEncryption(128);
        IAsymmetricKeyFileHandler asymmetricKeyFileHandler = new AsymmetricKeyFileHandler(localSignature);

        EncryptionService encryptionService = new EncryptionService(localSignature, remoteEncryption, localEncryption, asymmetricKeyFileHandler);
        encryptionService.inputMainPasswordAndInit("123");
        CertificateReaderWriter certificateReaderWriter = new CertificateFileReaderWriter(Paths.get(""));
        CertificatesService certificatesService = new CertificatesService(certificateReaderWriter, encryptionService.getLocalSymmetricEncryption(), new ResultBroadcaster());

        IdentityCertificate identityCertificate = new IdentityCertificate(new User(UUID.randomUUID(), "dd"), new byte[0][]);

        certificatesService.writeCertificate(identityCertificate);

        //encryptionService.inputMainPasswordAndInit("123");
        certificatesService.readCertificate();
    }
}