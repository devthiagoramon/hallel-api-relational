package br.hallel.relational.api.app.payment.checkout_transparent.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter
public class ProcessNotificationResponseDTO {
    private boolean success;
    private String status;
}
