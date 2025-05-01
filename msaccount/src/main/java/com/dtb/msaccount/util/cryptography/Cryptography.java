package com.dtb.msaccount.util.cryptography;

import com.dtb.msaccount.util.logging.Logging;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class Cryptography {

    private PrivateKey privateKey;
    private final Logging logging = new Logging();


    @PostConstruct
    public void init() throws Exception {
        ClassPathResource resource = new ClassPathResource("private_key.pem");
        try (InputStream is = resource.getInputStream()) {
            String key = new String(is.readAllBytes(), StandardCharsets.UTF_8)
                    .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.privateKey = keyFactory.generatePrivate(keySpec);
        }
    }

    public String decrypt(String base64Encrypted, String refId)  {
        String decryptedString =null;
        long startTime = System.currentTimeMillis();


        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(base64Encrypted);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decrypted = cipher.doFinal(encryptedBytes);
            decryptedString = new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logging
                    .setLogLevel("info")
                    .setTransactionID(refId)
                    .setProcess("Decrypt Password")
                    .setResponseMsg(e.getMessage())
                    .setProcessDuration(System.currentTimeMillis() - startTime)
                    .write();
        }

        return decryptedString;

    }
}
