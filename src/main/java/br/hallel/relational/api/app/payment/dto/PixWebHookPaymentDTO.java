package br.hallel.relational.api.app.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PixWebHookPaymentDTO {

    private String endToEndId;
    private String txid;
    private String valor;
    private String horario;


}
