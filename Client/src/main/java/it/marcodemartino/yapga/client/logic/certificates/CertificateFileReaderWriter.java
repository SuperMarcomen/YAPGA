package it.marcodemartino.yapga.client.logic.certificates;

import com.google.gson.Gson;
import it.marcodemartino.yapga.common.certificates.IdentityCertificate;
import it.marcodemartino.yapga.common.encryption.symmetric.SymmetricEncryption;
import it.marcodemartino.yapga.common.json.GsonInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Base64;

public class CertificateFileReaderWriter implements CertificateReaderWriter {

    private static final String CERTIFICATE_FILE_NAME = "identity_certificate.pem";
    private final Logger logger = LogManager.getLogger(CertificateFileReaderWriter.class);
    private final Gson gson;
    private final Path certificatePath;

    public CertificateFileReaderWriter(Path path) {
        gson = GsonInstance.get();
        certificatePath = getCertificatePath(path);
    }

    @Override
    public IdentityCertificate readCertificate() {
        String base64Json = tryReadCertificate();
        if (base64Json.isEmpty()) return null;
        return constructCertificate(base64Json);
    }

    @Override
    public IdentityCertificate readCertificate(SymmetricEncryption symmetricEncryption) {
        String encryptedBase64Json = tryReadCertificate();
        if (encryptedBase64Json.isEmpty()) return null;
        return constructCertificate(symmetricEncryption.decryptToString(encryptedBase64Json.getBytes(StandardCharsets.UTF_8)));
    }

    private IdentityCertificate constructCertificate(String base64Json) {
        byte[] jsonBytes = Base64.getDecoder().decode(base64Json.getBytes(StandardCharsets.UTF_8));
        String json = new String(jsonBytes, StandardCharsets.UTF_8);
        IdentityCertificate identityCertificate = gson.fromJson(json, IdentityCertificate.class);
        logger.info("Read a certificate for the user");
        return identityCertificate;
    }

    private String tryReadCertificate() {
        try {
             return Files.readString(certificatePath);
        } catch (IOException e) {
            logger.error("There was an error trying to read the certificate!", e);
        }
        return "";
    }

    @Override
    public boolean doesCertificateExist() {
        return Files.exists(certificatePath);
    }

    @Override
    public void writeCertificate(IdentityCertificate identityCertificate) {
        String json = gson.toJson(identityCertificate);
        String base64JSon = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        tryWriteCertificate(base64JSon);
    }

    @Override
    public void writeCertificate(IdentityCertificate identityCertificate, SymmetricEncryption symmetricEncryption) {
        String json = gson.toJson(identityCertificate);
        String decryptedBase64Json = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        String encryptedBase64Json = new String(symmetricEncryption.encryptFromString(decryptedBase64Json), StandardCharsets.UTF_8);
        tryWriteCertificate(encryptedBase64Json);
    }

    private void tryWriteCertificate(String base64JSon) {
        try {
            Files.writeString(certificatePath, base64JSon);
        } catch (IOException e) {
            logger.error("There was an error writing the certificate to file!", e);
        }
    }

    private Path getCertificatePath(Path path) {
        return Paths.get(path.toAbsolutePath().toString(), CERTIFICATE_FILE_NAME);
    }
}
