package br.hallel.relational.api.app.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventCashFlowResponse {
    private UUID eventId;
    private Double totalRecipe;
    private Double totalExpense;
    private Double difference;
    private LocalDateTime date;
    private List<EventTransactionResponse> transaction;
}
