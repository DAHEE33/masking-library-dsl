package com.masking.aciton;

import com.masking.strategy.encrypt.EncryptionStrategy;
import com.masking.strategy.encrypt.RsaEncryptionStrategy;
import org.junit.jupiter.api.Test;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import static org.junit.jupiter.api.Assertions.*;
import com.masking.util.CryptoUtil;
import javax.crypto.Cipher;
import java.util.Base64;

public class RsaEncryptionStrategyTest {
    @Test
    void rsaEncrypt_decrypt_roundTrip() throws Exception {
        KeyPair kp = CryptoUtil.generateRsaKeyPair();
        PublicKey pub = kp.getPublic();
        PrivateKey priv = kp.getPrivate();

        EncryptionStrategy enc = RsaEncryptionStrategy.of(pub);
        String original = "secretData";
        String encrypted = enc.encrypt(original);
        assertNotNull(encrypted);

        // 복호화 검증
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, priv);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encrypted));
        String decrypted = new String(decryptedBytes, "UTF-8");
        assertEquals(original, decrypted);
    }
}
