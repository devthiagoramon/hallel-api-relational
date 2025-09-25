package br.hallel.relational.api.app.integrationtests.ministry.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@SuppressWarnings({"LombokGetterMayBeUsed", "LombokSetterMayBeUsed"})
public class TokenCoordinatorDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID userId;
    private UUID ministryId;
    private Date expiration;
    private String accessToken;
    private String refreshToken;

    public TokenCoordinatorDTO() {
    }

    public TokenCoordinatorDTO(UUID userId, UUID ministryId,
                               Date expiration, String accessToken,
                               String refreshToken) {
        this.userId = userId;
        this.ministryId = ministryId;
        this.expiration = expiration;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getMinistryId() {
        return ministryId;
    }

    public void setMinistryId(UUID ministryId) {
        this.ministryId = ministryId;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
