package br.hallel.relational.api.app.ministry.controller.coordinator;

import br.hallel.relational.api.app.ministry.dto.ScaleChatParticipantResponse;
import br.hallel.relational.api.app.ministry.model.ScaleChatParticipant;
import br.hallel.relational.api.app.ministry.service.ScaleChatParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coordinator/event/scale/chat")
@Tag(name = "Coordinator scale chat", description = "Coordinator part for scale chat, when controls important actions in scale")
public class CoordinatorScaleChatController {

    private final ScaleChatParticipantService participantService;

    @PostMapping("/{scale-id}")
    @Operation(summary = "Create chat of scale", description = "Route to create scale of chat, adding all users in chat of scale")
    public ResponseEntity<List<ScaleChatParticipantResponse>> createChatOfScale(@PathVariable("scale-id") UUID scaleId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.participantService.createScaleChat(scaleId));
    }

    @DeleteMapping("/{scale-id}")
    public ResponseEntity<?> deleteChatOfScale(@PathVariable("scale-id") UUID scaleId) {
        this.participantService.deleteScaleChat(scaleId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/add/participant")
    @Operation(summary = "Add participant in some chat", description = "Handles adding some participant of scale in chat")
    public ResponseEntity<ScaleChatParticipant> addParticipantOfScaleChat(@RequestParam(name = "user-id") UUID userId,
                                                                          @RequestParam(name = "scale-id")
                                                                          UUID scaleId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.participantService.addParticipantFromScaleChatParticipant(userId, scaleId));
    }

    @DeleteMapping("/remove/participant/{participant-chat-id}")
    public ResponseEntity<Boolean> removeParticipantOfScaleChat(
            @PathVariable(name = "participant-chat-id") UUID participantChatId) {
        this.participantService.removeParticipantFromScaleChat(participantChatId);
        return ResponseEntity.noContent().build();
    }


}
