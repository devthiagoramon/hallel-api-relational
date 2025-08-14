package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.EventTransaction;
import br.hallel.relational.api.app.event.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventTransactionResponse {

    private UUID id;
    private String description;
    private double value;
    private TransactionType transactionType;
    private UUID eventId;

    public EventTransactionResponse toResponse(EventTransaction eventTransaction) {
        return new EventTransactionResponse(
                eventTransaction.getId(),
                eventTransaction.getDesciption(),
                eventTransaction.getValue(),
                eventTransaction.getTransactionType(),
                eventTransaction.getEvent().getId()
        );
    }

}
