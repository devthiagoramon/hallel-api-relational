package br.hallel.relational.api.app.ministry.dto;

import br.hallel.relational.api.app.ministry.model.FunctionMinistry;
import br.hallel.relational.api.app.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberMinistryResponseWithFunctions {
    private UUID id;
    private User user;
    private List<FunctionMinistry> functionMember;
}
