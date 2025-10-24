package br.hallel.relational.api.app.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DateBirthUserDTO {
    private OffsetDateTime dateBirth;
}
