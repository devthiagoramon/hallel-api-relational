package br.hallel.relational.api.app.auth.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Data
@Getter @Setter @NoArgsConstructor
public class SignUpRequest {
    private String name;
    private String email;
    private String password;
    private Date birthDate;
}
