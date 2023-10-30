package it.marcodemartino.yapga.client.logic.certificates;

public interface CertificateReaderWriter extends CertificateReader, CertificateWriter {

    boolean doesCertificateExist();

}
