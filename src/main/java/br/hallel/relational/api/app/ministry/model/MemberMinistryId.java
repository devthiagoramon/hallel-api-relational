package br.hallel.relational.api.app.ministry.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class MemberMinistryId {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "ministry_id")
    private UUID ministryId;

    public MemberMinistryId() {
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID memberId) {
        this.userId = memberId;
    }

    public UUID getMinistryId() {
        return ministryId;
    }

    public void setMinistryId(UUID ministryId) {
        this.ministryId = ministryId;
    }

    public MemberMinistryId(UUID userId, UUID ministryId) {
        this.userId = userId;
        this.ministryId = ministryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberMinistryId that = (MemberMinistryId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(ministryId, that.ministryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, ministryId);
    }
}
