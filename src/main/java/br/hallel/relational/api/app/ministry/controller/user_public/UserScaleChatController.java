package br.hallel.relational.api.app.ministry.controller.user_public;

import br.hallel.relational.api.app.event.dto.EventResponse;
import br.hallel.relational.api.app.ministry.dto.ScaleChatInfoResponse;
import br.hallel.relational.api.app.ministry.dto.ScaleChatParticipantUserResponse;
import br.hallel.relational.api.app.ministry.service.ScaleChatParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/event/scale/chat")
@Tag(name = "User scale chat", description = "User part for scale chat, for actions in scale")
public class UserScaleChatController {

    private final ScaleChatParticipantService scaleChatParticipantService;

    @GetMapping("/participants/{scale-id}")
    @Operation(summary = "List participants of scale chat", description = "Route to list all participants of scale chat")
    public ResponseEntity<List<ScaleChatParticipantUserResponse>> listParticipantsOfScaleChat(@PathVariable("scale-id") UUID scaleId) {
        return ResponseEntity.ok().body(this.scaleChatParticipantService.listParticipantsScaleChat(scaleId));
    }

    @GetMapping("/exists/{scale-id}")
    @Operation(summary = "Verify existence of scale chat", description = "Route to verify if had created some chat for scale")
    public ResponseEntity<Boolean> verifyIfScaleChatExists(@PathVariable("scale-id") UUID scaleId) {
        return ResponseEntity.ok().body(this.scaleChatParticipantService.verifyIfScaleChatExists(scaleId));
    }

    @GetMapping("/info/{scale-id}")
    @Operation(summary = "Get the important infos of scale chat")
    public ResponseEntity<ScaleChatInfoResponse> getScaleChatInfo(@PathVariable("scale-id") UUID scaleId) {
        return ResponseEntity.ok().body(this.scaleChatParticipantService.getInfoOfScaleChat(scaleId));
    }


}
