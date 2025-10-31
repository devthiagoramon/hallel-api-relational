package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.EventTransaction;
import br.hallel.relational.api.app.event.model.enum_type.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
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
    private Date dateTransaction;
    private Boolean isEditable;


    public static EventTransactionResponse toResponse(EventTransaction eventTransaction) {
        return new EventTransactionResponse(
                eventTransaction.getId(),
                eventTransaction.getDescription(),
                eventTransaction.getValue(),
                eventTransaction.getTransactionType(),
                eventTransaction.getEvent().getId(),
                eventTransaction.getDateTransaction(),
                eventTransaction.getIsEditable()
        );
    }

}
