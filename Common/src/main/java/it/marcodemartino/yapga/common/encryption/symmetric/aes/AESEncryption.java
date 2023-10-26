package it.marcodemartino.yapga.common.encryption.symmetric.aes;

import it.marcodemartino.yapga.common.encryption.symmetric.SymmetricEncryption;
import it.marcodemartino.yapga.common.encryption.symmetric.SymmetricKeyContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AESEncryption implements SymmetricEncryption {

    private static final String ALGORITHM_INSTANCE = "AES/CBC/PKCS5Padding";
    private final int saltSize;
    private final Logger logger = LogManager.getLogger(AESEncryption.class);
    private final int keySize;
    private KeyGenerator keyGenerator;
    private SymmetricKeyContainer symmetricKeyContainer;
    private Cipher encryptCipher;
    private Cipher decryptCipher;

    public AESEncryption(int keySize) {
        this.keySize = keySize;
        this.saltSize = keySize / 8;
        tryInitKeyGenerator();
        tryInitCipher();
    }

    @Override
    public byte[] encryptFromString(String input) {
        return encrypt(input.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public byte[] encrypt(byte[] input) {
        try {
            return encryptCipher.doFinal(input);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            logger.error("There was an error while encrypting the input");
            return new byte[0];
        }
    }

    @Override
    public String decryptToString(byte[] input) {
        return new String(decrypt(input), StandardCharsets.UTF_8);
    }

    @Override
    public byte[] decrypt(byte[] input) {
        try {
            return decryptCipher.doFinal(input);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            logger.error("There was an error while decrypting the input");
            return new byte[0];
        }
    }

    @Override
    public void generateKey() {
        IvParameterSpec ivParameterSpec = generateIv();
        SecretKey secretKey = keyGenerator.generateKey();
        symmetricKeyContainer = new AESSymmetricKeyContainer(secretKey, ivParameterSpec);
        initCiphers(ivParameterSpec, secretKey);
    }

    @Override
    public void generateKeyFromPassword(String password, byte[] salt) {
        try {
            int iterations = 10000;
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, keySize);

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] derivedKey = keyFactory.generateSecret(keySpec).getEncoded();

            SecretKey secretKey = new SecretKeySpec(derivedKey, "AES");
            IvParameterSpec ivParameterSpec = generateIv();
            symmetricKeyContainer = new AESSymmetricKeyContainer(secretKey, ivParameterSpec);
            initCiphers(ivParameterSpec, secretKey);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            logger.error("There was an error generating a key from a password", e);
        }
    }

    @Override
    public byte[] generateSalt() {
        byte[] salt = new byte[saltSize];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    @Override
    public SymmetricKeyContainer getKey() {
        return symmetricKeyContainer;
    }

    private IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    @Override
    public void setKey(SymmetricKeyContainer secretKey) {
        this.symmetricKeyContainer = secretKey;
    }

    @Override
    public SymmetricKeyContainer constructKeyFromString(String key) {
        String[] args = key.split(System.lineSeparator());
        byte[] keyBytes = Base64.getDecoder().decode(args[0]);
        byte[] ivBytes = Base64.getDecoder().decode(args[1]);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
        return new AESSymmetricKeyContainer(secretKey, ivParameterSpec);
    }

    @Override
    public String keyToString(SymmetricKeyContainer symmetricKeyContainer) {
        String encodedKey = Base64.getEncoder().encodeToString(symmetricKeyContainer.getSecretKey().getEncoded());
        String encodedIV = Base64.getEncoder().encodeToString(symmetricKeyContainer.getIV().getIV());
        return encodedKey + System.lineSeparator() + encodedIV;
    }

    private void initCiphers(IvParameterSpec ivParameterSpec, SecretKey secretKey) {
        try {
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            logger.error("Could not init the cipher", e);
        }
    }

    private void tryInitCipher() {
        try {
            encryptCipher = Cipher.getInstance(ALGORITHM_INSTANCE);
            decryptCipher = Cipher.getInstance(ALGORITHM_INSTANCE);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            logger.fatal("Could not init the cipher, quitting the app!", e);
            System.exit(1);
        }
    }

    private void tryInitKeyGenerator() {
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(keySize);
        } catch (NoSuchAlgorithmException e) {
            logger.fatal("Could not init the key generator, quitting the app!", e);
            System.exit(1);
        }
    }
}
