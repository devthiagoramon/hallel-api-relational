package br.hallel.relational.api.app.payment.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Credentials {
    @Value("${pix.client.id}")
    private String clientId;

    @Value("${pix.client.secret}")
    private String clientSecret;

    @Value("${pix.certificate.path}")
    private String certificate;

    @Value("${pix.sandbox}")
    private boolean sandbox;

    @Value("${pix.debug}")
    private boolean debug;

    public Credentials() {
        // Construtor vazio para o Spring Boot
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public boolean isSandbox() {
        return sandbox;
    }

    public void setSandbox(boolean sandbox) {
        this.sandbox = sandbox;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}