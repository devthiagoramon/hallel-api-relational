package br.hallel.relational.api.app.ministry.controller.user_public;

import br.hallel.relational.api.app.ministry.service.ScaleChatMessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/event/scale/chat/message")
@Tag(name = "User scale chat message", description = "User part for scale chat message, for actions in scale to send message")
@RequiredArgsConstructor
public class UserScaleChatMessageController {

    private final ScaleChatMessageService scaleChatMessageService;

}
