package br.hallel.relational.api.app.association.dto;

import br.hallel.relational.api.app.association.model.AssociatePaymentStatus;

public record AssociatePayDetails (
        String qrCode,
        String copyAndPaste,
        double value,
        AssociatePaymentStatus status
){
}
