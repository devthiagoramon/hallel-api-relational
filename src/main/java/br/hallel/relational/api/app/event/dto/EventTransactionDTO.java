package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.TransactionType;

import java.util.UUID;

public record EventTransactionDTO(
        UUID id,
        String desciption,
        TransactionType transactionType,
        Double value,
        UUID eventID) {
}
