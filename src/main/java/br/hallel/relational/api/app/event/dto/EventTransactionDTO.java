package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.TransactionType;

import java.util.Date;
import java.util.UUID;

public record EventTransactionDTO(
        String desciption,
        TransactionType transactionType,
        Double value,
        Date dateTransaction,
        UUID eventID
) {
}
