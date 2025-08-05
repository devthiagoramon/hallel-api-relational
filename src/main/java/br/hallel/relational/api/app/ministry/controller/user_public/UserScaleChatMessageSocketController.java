package br.hallel.relational.api.app.ministry.controller.user_public;

import br.hallel.relational.api.app.ministry.dto.ScaleChatMessageRequest;
import br.hallel.relational.api.app.ministry.dto.ScaleChatMessageResponse;
import br.hallel.relational.api.app.ministry.model.ScaleChatMessage;
import br.hallel.relational.api.app.ministry.service.ScaleChatMessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/socket/scale/chat")
@Tag(name = "User scale chat message", description = "Handles with real-time messaging in scale chat")
@RequiredArgsConstructor
public class UserScaleChatMessageSocketController {

    public final ScaleChatMessageService scaleChatMessageService;

    @MessageMapping("/text")
    public ResponseEntity<ScaleChatMessageResponse> sendTextMessage(@Payload ScaleChatMessageRequest request){
        return ResponseEntity.ok(scaleChatMessageService.sendTextMessage(request));
    }

}
