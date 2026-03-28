package br.hallel.relational.api.app.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateEditUser {
    private String name;
    private String email;
    private String password;
    private String cpf;
    private Date dateBirth;
    private String phoneNumber;
    private List<String> roles;
}
