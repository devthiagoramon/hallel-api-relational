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
        String pepper =
                "0b5c1b6a6f0e9f1e1b2c3d4a5b6c7d8e9f0a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e";

        Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new
                Pbkdf2PasswordEncoder(pepper,
                16,
                185000,
                Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);

        encoders.put("pbkdf2", pbkdf2PasswordEncoder);
        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2PasswordEncoder);

        String admEncode = passwordEncoder.encode("hallel2023");
        String user1Enconde = passwordEncoder.encode("barros123");
        String user2Enconde = passwordEncoder.encode("miguel123");
        String user3Enconde = passwordEncoder.encode("felipe123");
        String user4Enconde = passwordEncoder.encode("emmerson123");
        String user5Enconde = passwordEncoder.encode("manfred123");
        String user6Enconde = passwordEncoder.encode("carlamuniqueadmhallel05!");
        String user7Enconde = passwordEncoder.encode("thiagoramonadmhallel2025!");
        String user8Encode = passwordEncoder.encode("emmersonadmhallel2025!");
        String user9Encode = passwordEncoder.encode("miguelarcanjoadm2025!");
        String user10Encode = passwordEncoder.encode("leonardolopesadm2025!");
        String user11Encode = passwordEncoder.encode("tannycavalcanteadm2025!");

        System.out.println("Adm hash " + admEncode);
        System.out.println("user 1 hash  " + user1Enconde);
        System.out.println("user 2 hash  " + user2Enconde);
        System.out.println("user 3 hash  " + user3Enconde);
        System.out.println("user 4 hash  " + user4Enconde);
        System.out.println("user 5 hash  " + user5Enconde);
        System.out.println("user 6 hash  " + user6Enconde);
        System.out.println("user 7 hash  " + user7Enconde);
        System.out.println("user 8 hash  " + user8Encode);
        System.out.println("user 9 hash  " + user9Encode);
        System.out.println("user 10 hash  " + user10Encode);
        System.out.println("user 11 hash  " + user11Encode);

    }

}
