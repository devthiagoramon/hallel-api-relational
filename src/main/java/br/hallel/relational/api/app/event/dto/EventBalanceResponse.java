package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.event.model.BalanceType;
import br.hallel.relational.api.app.event.model.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventBalanceResponse {
    private UUID eventId;
    private EventType eventType;
    private double profit;
    private double prejudice;
    private double total;
    private BalanceType balanceType;
}
