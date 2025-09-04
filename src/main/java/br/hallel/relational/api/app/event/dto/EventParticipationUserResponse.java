package br.hallel.relational.api.app.event.dto;

import br.hallel.relational.api.app.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventParticipationUserResponse {
    private User user;

}
