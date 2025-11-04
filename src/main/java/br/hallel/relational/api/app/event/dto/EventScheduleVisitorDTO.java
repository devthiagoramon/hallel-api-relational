// Crie este arquivo em: br/hallel/relational/api/app/event/dto/EventScheduleVisitorDTO.java
package br.hallel.relational.api.app.event.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventScheduleVisitorDTO {

    @NotBlank(message = "O nome do visitante é obrigatório")
    private String name;

    @Email(message = "Email do visitante inválido")
    @NotBlank(message = "O email do visitante é obrigatório")
    private String email;

    private String phoneNumber;

    private OffsetDateTime dateBirth;
}