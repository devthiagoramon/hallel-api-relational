package br.hallel.relational.api.app.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenAdminResponse {
    private String tokenAdmin;
    private String code;
}
