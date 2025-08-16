package br.hallel.relational.api.app.payment.config;


import br.hallel.relational.api.app.payment.model.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

@Configuration
public class SslConfiguration implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Autowired
    private Credentials credentials; // Verifique se esta classe tem o campo certificateContent

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        try {
            File tempFile = createTempCertificateFile(credentials.getCertificateContent());

            Ssl ssl = new Ssl();
            ssl.setKeyStoreType("PKCS12");
            ssl.setKeyStore(tempFile.getAbsolutePath());
            ssl.setKeyStorePassword(""); // A SENHA AGORA É VAZIA
            ssl.setKeyAlias("alias_do_seu_certificado"); // Substitua pelo alias correto do seu certificado
            ssl.setClientAuth(Ssl.ClientAuth.WANT);
            factory.setSsl(ssl);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao configurar SSL para mTLS.");
        }
    }

    private File createTempCertificateFile(String content) throws IOException {
        File tempFile = File.createTempFile("hallel-certificado", ".p12");
        tempFile.deleteOnExit();
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            byte[] decodedBytes = Base64.getDecoder().decode(content);
            fos.write(decodedBytes);
        }
        return tempFile;
    }
}
