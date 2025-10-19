package br.hallel.relational.api.app.association.dto;

import br.hallel.relational.api.app.association.model.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record CreateAssociateRequestDTO(

        @NotNull(message = "The field 'PAYMENTMETHOD' cannot be null")
        PaymentMethod paymentMethod,
        @DecimalMin(value = "0.01", inclusive = false, message = "The field 'VALUEASSOCIATION' must be greater than 0")
        @NotNull(message = "The field 'VALUEASSOCIATION' cannot be null")
        Double valueAssociation
) {
}
