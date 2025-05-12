package br.hallel.relational.api.app.user.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class MemberMinistryId implements Serializable {

    private UUID member;
    private UUID ministry;

    // Getters, Setters, equals() e hashCode()

}