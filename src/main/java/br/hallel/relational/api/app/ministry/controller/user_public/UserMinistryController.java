package br.hallel.relational.api.app.ministry.controller.user_public;

import br.hallel.relational.api.app.ministry.model.StatusParticipationMinistry;
import br.hallel.relational.api.app.ministry.service.MinistryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/user/ministry/")
@RequiredArgsConstructor
@Tag(name = "User ministry", description = "User part for ministry funcionalities")
public class UserMinistryController {

    private final MinistryService ministryService;

    @GetMapping("/status/{ministryId}/{memberMinistryId}")
    @Operation(summary = "List user status in some minsitry", description = "Route to list the status of user in ministry")
    public ResponseEntity<StatusParticipationMinistry> getUserStatusParticipationMinistry(@PathVariable("ministryId")
                                                                                          UUID ministryId,
                                                                                          @PathVariable("memberMinistryId") UUID memberMinistryId) {
        return ResponseEntity.ok().body(this.ministryService.listStatusParticipationInMinistry(ministryId, memberMinistryId));

    }

}
