package br.hallel.relational.api.app.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Configuration
public class GcpConfig {

    private static final String TEMP_CREDENTIALS_PATH = System.getProperty("java.io.tmpdir") + "/gcp-key.json";

    @PostConstruct
    public void init() throws Exception {
        InputStream inputStream = getClass().getClassLoader()
                                            .getResourceAsStream("crypto-avatar.json");
        if (inputStream == null) {
            throw new RuntimeException("Arquivo de credenciais não encontrado no resources!");
        }
        Path tempPath = Paths.get(TEMP_CREDENTIALS_PATH).getParent();
        if (tempPath != null) {
            Files.createDirectories(tempPath);
        }

        File tempFile = new File(TEMP_CREDENTIALS_PATH);
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", TEMP_CREDENTIALS_PATH);

        }


    }

    @Bean
    public Storage storage() throws IOException {
        return StorageOptions.newBuilder()
                             .setCredentials(GoogleCredentials.fromStream(Objects.requireNonNull(getClass().getClassLoader()
                                                                                                           .getResourceAsStream("crypto-avatar.json"))))
                             .build().getService();

    }

}
