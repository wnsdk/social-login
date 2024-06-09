package com.example.user.jasypt;

import com.example.user.global.config.JasyptConfig;
import org.assertj.core.api.Assertions;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

public class JasyptTest extends JasyptConfig {
    @Test
    public void jasypt_encrypt_decrypt_test() {
        String plainText = "plain text";

        StandardPBEStringEncryptor jasypt = new StandardPBEStringEncryptor();
        jasypt.setPassword("password");

        String encryptedText = jasypt.encrypt(plainText);
        String decryptedText = jasypt.decrypt(encryptedText);

        System.out.println(encryptedText);

        Assertions.assertThat(plainText).isEqualTo(decryptedText);
    }
}
