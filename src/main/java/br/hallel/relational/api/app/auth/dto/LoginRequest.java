package br.hallel.relational.api.app.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotNull(message = "E-mail cannot be null")
    @NotBlank(message = "Insert e-mail")
    @Email(message = "Insert a valid e-mail")
    private String email;

    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Insert password")
    private String password;

}
