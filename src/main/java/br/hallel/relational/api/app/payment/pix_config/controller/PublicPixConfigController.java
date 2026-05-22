package br.hallel.relational.api.app.payment.pix_config.controller;

import br.hallel.relational.api.app.payment.pix_config.dto.PixConfigResponseDTO;
import br.hallel.relational.api.app.payment.pix_config.service.PixConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/public/pix-config")
@RequiredArgsConstructor
@Tag(name = "PIX Config - Public", description = "Rota pública para obter a configuração PIX ativa")
public class PublicPixConfigController {

    private final PixConfigService pixConfigService;

    @GetMapping("/active")
    @Operation(summary = "Obter a configuração PIX ativa")
    public ResponseEntity<PixConfigResponseDTO> getActive() {
        return ResponseEntity.ok(pixConfigService.getActive());
    }
}
