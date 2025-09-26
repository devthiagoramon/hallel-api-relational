package br.hallel.relational.api.app.ministry.controller.user_public;

import br.hallel.relational.api.app.ministry.dto.ScaleChatMessageRequest;
import br.hallel.relational.api.app.ministry.dto.ScaleChatMessageRequestEdit;
import br.hallel.relational.api.app.ministry.dto.ScaleChatMessageResponse;
import br.hallel.relational.api.app.ministry.dto.StatusReadingMessageUserResponse;
import br.hallel.relational.api.app.ministry.model.MessageScaleDeliveryStatus;
import br.hallel.relational.api.app.ministry.service.ScaleChatMessageService;
import br.hallel.relational.api.app.security.utils.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user/event/scale/chat/message")
@Tag(name = "User scale chat message", description = "User part for scale chat message, for actions in scale to send message")
@RequiredArgsConstructor
public class UserScaleChatMessageController {

    private final ScaleChatMessageService scaleChatMessageService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping(path = "/file", consumes = {"multipart/form-data"})
    @Operation(summary = "Send message as file to scale chat", description = "Handles when user send file message to some scale chat passing scale id and request, returning the message")
    public ResponseEntity<ScaleChatMessageResponse> sendFileMessageToScaleChat(@RequestPart(name = "file")
                                                                               MultipartFile file,
                                                                               @RequestPart(name = "request")
                                                                               ScaleChatMessageRequest scaleChatMessageRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.scaleChatMessageService.sendFileMessage(file, scaleChatMessageRequest));
    }

    @PostMapping("/text")
    @Operation(summary = "Send message as text to scale chat", description = "Handles when user send message to some scale chat passing scale id and request, returning the message")
    public ResponseEntity<ScaleChatMessageResponse> sendTextMessage(@RequestBody ScaleChatMessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scaleChatMessageService.sendTextMessage(request));
    }

    @PutMapping("/edit")
    @Operation(summary = "Edit message of scale chat", description = "Handles when user edit message to some scale chat passing scale id and request, returning the message")
    public ResponseEntity<ScaleChatMessageResponse> updateMessage(@RequestBody ScaleChatMessageRequestEdit request) {
        return ResponseEntity.ok(scaleChatMessageService.editMessage(request));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete message of scale chat", description = "Handles when user delete message to some scale chat passing scale id and request, returning the message deleted")
    public ResponseEntity<ScaleChatMessageResponse> deleteMessage(@RequestParam UUID messageId) {
        return ResponseEntity.ok(this.scaleChatMessageService.deleteMessage(messageId));
    }

    @GetMapping("/{scale-id}")
    @Operation(summary = "List messages from some scale with status", description = "Handles when user wants to get the messages of scale chat, returning the status of delivering of his messages")
    public ResponseEntity<Page<ScaleChatMessageResponse>> listMessageOfScaleChatForUser(
            @RequestHeader("Authorization") String authorizationHeader, @PathVariable("scale-id") UUID scaleId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        UUID userId = jwtTokenProvider.getUserId(authorizationHeader);
        return ResponseEntity.ok(this.scaleChatMessageService.listMessagesOfScaleChatForUser(scaleId, userId,
                PageRequest.of(page, size)));
    }

    @GetMapping("/status-reading/{message-id}")
    @Operation(summary = "List status reading per user", description = "Handles listing status of delivering per user")
    public ResponseEntity<List<StatusReadingMessageUserResponse>> listStatusReadingPerUserInChatByMessage(
            @PathVariable("message-id") UUID messageId) {
        return ResponseEntity.ok(this.scaleChatMessageService.listStatusDeliveryPerUser(messageId));
    }

    @PatchMapping("/read-message/{message-id}")
    @Operation(summary = "User read message", description = "Handles when user reads message")
    public ResponseEntity<MessageScaleDeliveryStatus> readMessageForUser(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("message-id") UUID messageId) {
        UUID userId = jwtTokenProvider.getUserId(authorizationHeader);
        return ResponseEntity.ok(this.scaleChatMessageService.readMessageForUser(userId, messageId));
    }

    @PatchMapping("/receive-message/{message-id}")
    @Operation(summary = "User received message", description = "Handles when user receive message")
    public ResponseEntity<MessageScaleDeliveryStatus> userReceivedMessage(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("message-id") UUID messageId) {
        UUID userId = jwtTokenProvider.getUserId(authorizationHeader);
        return ResponseEntity.ok(this.scaleChatMessageService.userReceivedMessage(messageId, userId));
    }
}
