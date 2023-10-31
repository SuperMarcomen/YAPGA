package it.marcodemartino.yapga.client.logic.certificates;

import it.marcodemartino.yapga.common.certificates.IdentityCertificate;
import it.marcodemartino.yapga.common.encryption.symmetric.SymmetricEncryption;

public interface CertificateWriter {

    void writeCertificate(IdentityCertificate identityCertificate);
    void writeCertificate(IdentityCertificate identityCertificate, SymmetricEncryption symmetricEncryption);

}
