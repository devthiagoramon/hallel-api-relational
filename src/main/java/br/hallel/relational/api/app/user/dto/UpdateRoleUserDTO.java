package br.hallel.relational.api.app.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateRoleUserDTO {
    private UUID userId;
    private List<String> roleNameAdd;
    private List<String> roleNameRemove;
}
