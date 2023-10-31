package it.marcodemartino.yapga.client.logic.certificates;

import it.marcodemartino.yapga.common.certificates.IdentityCertificate;
import it.marcodemartino.yapga.common.encryption.symmetric.SymmetricEncryption;

public interface CertificateReader {

    IdentityCertificate readCertificate();
    IdentityCertificate readCertificate(SymmetricEncryption symmetricEncryption);


}
