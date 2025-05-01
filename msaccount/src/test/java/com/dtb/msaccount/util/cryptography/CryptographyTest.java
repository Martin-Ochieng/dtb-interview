package com.dtb.msaccount.util.cryptography;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;



import org.junit.jupiter.api.BeforeEach;

import javax.crypto.Cipher;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

class CryptographyTest {

    private Cryptography cryptography;
    private PublicKey publicKey;

    @BeforeEach
    void setUp() throws Exception {
        // Generate a test RSA key pair
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();

        // Create Cryptography instance and inject the private key
        cryptography = new Cryptography();
        var privateKeyField = Cryptography.class.getDeclaredField("privateKey");
        privateKeyField.setAccessible(true);
        privateKeyField.set(cryptography, privateKey);
    }

    @Test
    void decrypt_shouldReturnOriginalString_whenValidEncryptedInputProvided() throws Exception {
        // Original plaintext
        String original = "SuperSecret123";

        // Encrypt with public key
        Cipher encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = encryptCipher.doFinal(original.getBytes(StandardCharsets.UTF_8));
        String base64Encrypted = Base64.getEncoder().encodeToString(encryptedBytes);

        // Decrypt using the method
        String decrypted = cryptography.decrypt(base64Encrypted, "ref123");

        // Assert correctness
        assertEquals(original, decrypted);
    }

    @Test
    void decrypt_shouldReturnNull_whenBase64Invalid() {
        String invalidBase64 = "!!!not_base64===";

        String result = cryptography.decrypt(invalidBase64, "ref456");

        assertNull(result);
    }

    @Test
    void decrypt_shouldReturnNull_whenCipherDataCorrupted() {
        // Random bytes (not valid encrypted RSA data)
        byte[] garbage = new byte[]{1, 2, 3, 4, 5};
        String fakeEncrypted = Base64.getEncoder().encodeToString(garbage);

        String result = cryptography.decrypt(fakeEncrypted, "ref789");

        assertNull(result);
    }
    @Test
    void init_shouldLoadPrivateKeyFromClasspathPEM() throws Exception {
        // Given
        Cryptography cryptography = new Cryptography();

        // When
        cryptography.init();

        // Then
        Field field = Cryptography.class.getDeclaredField("privateKey");
        field.setAccessible(true);
        PrivateKey key = (PrivateKey) field.get(cryptography);

        assertNotNull(key, "Private key should be initialized");
        assertEquals("RSA", key.getAlgorithm(), "Key algorithm should be RSA");
    }
}
