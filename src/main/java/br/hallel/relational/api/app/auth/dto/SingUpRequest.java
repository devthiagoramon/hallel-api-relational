package br.hallel.relational.api.app.auth.dto;

import br.hallel.relational.api.app.security.model.Role;
import br.hallel.relational.api.app.user.model.UserStatus;
import lombok.*;

import java.util.Set;

@Data
@Getter @Setter @NoArgsConstructor
public class SingUpRequest {
    private String name;
    private String email;
    private String password;

    public SingUpRequest(String password, String email, String name) {
        this.password = password;
        this.email = email;
        this.name = name;
    }
}
