package br.hallel.relational.api.app.ministry.controller.user_public;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/scale/chat")
@Tag(name = "Socket for scale chat", description = "Handles with real-time messaging in scale chat")
@RequiredArgsConstructor
public class UserScaleChatMessageSocketController {

}
