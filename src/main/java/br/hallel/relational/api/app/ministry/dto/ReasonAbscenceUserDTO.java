package br.hallel.relational.api.app.ministry.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReasonAbscenceUserDTO {
    @NotBlank(message = "Type a reason to absence of user")
    @NotNull(message = "Type have to been passed")
    private String reason;
}
