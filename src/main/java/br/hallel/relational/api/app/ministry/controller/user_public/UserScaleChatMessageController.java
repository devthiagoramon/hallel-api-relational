package br.hallel.relational.api.app.ministry.controller.user_public;

import br.hallel.relational.api.app.ministry.dto.ScaleChatMessageRequest;
import br.hallel.relational.api.app.ministry.dto.ScaleChatMessageResponse;
import br.hallel.relational.api.app.ministry.service.ScaleChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user/event/scale/chat/message")
@Tag(name = "User scale chat message", description = "User part for scale chat message, for actions in scale to send message")
@RequiredArgsConstructor
public class UserScaleChatMessageController {

    private final ScaleChatMessageService scaleChatMessageService;

    @PostMapping(path = "/file", consumes = {"multipart/form-data"})
    @Operation(summary = "Send message as file to scale chat", description = "Handles when user send file message to some scale chat passing scale id and request, returning the message")
    public ResponseEntity<ScaleChatMessageResponse> sendFileMessageToScaleChat(@RequestPart(name = "file")
                                                                               MultipartFile file,
                                                                               @RequestPart(name = "request")
                                                                               ScaleChatMessageRequest scaleChatMessageRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.scaleChatMessageService.sendFileMessage(file, scaleChatMessageRequest));
    }



}
