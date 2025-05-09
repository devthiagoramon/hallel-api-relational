package br.hallel.relational.api.app;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

public class PasswordHashCreateTest {

    @Test
    void generateHashs(){
        Map<String, PasswordEncoder> encoders = new HashMap<>();

        Pbkdf2PasswordEncoder pbkdf2Encoder =
                new Pbkdf2PasswordEncoder(
                        "", 8, 185000,
                        Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);

        encoders.put("pbkdf2", pbkdf2Encoder);
        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2Encoder);

        String admEncode = passwordEncoder.encode("hallel2023");
        String user1Enconde = passwordEncoder.encode("barros123");
        String user2Enconde = passwordEncoder.encode("miguel123");
        String user3Enconde = passwordEncoder.encode("felipe123");
        String user4Enconde = passwordEncoder.encode("emmerson123");
        String user5Enconde = passwordEncoder.encode("manfred123");

        System.out.println("Adm hash " + admEncode);
        System.out.println("user 1 hash  " + user1Enconde);
        System.out.println("user 2 hash  " + user2Enconde);
        System.out.println("user 3 hash  " + user3Enconde);
        System.out.println("user 4 hash  " + user4Enconde);
        System.out.println("user 5 hash  " + user5Enconde);
    }

}
