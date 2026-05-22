package br.hallel.relational.api.app.payment.pix_config.controller;

import br.hallel.relational.api.app.payment.pix_config.dto.CreatePixConfigRequestDTO;
import br.hallel.relational.api.app.payment.pix_config.dto.PixConfigResponseDTO;
import br.hallel.relational.api.app.payment.pix_config.dto.UpdatePixConfigRequestDTO;
import br.hallel.relational.api.app.payment.pix_config.service.PixConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/pix-config")
@RequiredArgsConstructor
@Tag(name = "PIX Config - Admin", description = "Rotas administrativas para configuração de chave PIX")
public class AdminPixConfigController {

    private final PixConfigService pixConfigService;

    @GetMapping
    @Operation(summary = "Listar todas as configurações PIX")
    public ResponseEntity<List<PixConfigResponseDTO>> listAll() {
        return ResponseEntity.ok(pixConfigService.listAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar configuração PIX por ID")
    public ResponseEntity<PixConfigResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(pixConfigService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Criar nova configuração PIX")
    public ResponseEntity<PixConfigResponseDTO> create(
            @RequestBody @Valid CreatePixConfigRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pixConfigService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar configuração PIX")
    public ResponseEntity<PixConfigResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdatePixConfigRequestDTO dto) {
        return ResponseEntity.ok(pixConfigService.update(id, dto));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Ativar configuração PIX")
    public ResponseEntity<PixConfigResponseDTO> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(pixConfigService.activate(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir configuração PIX")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        pixConfigService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
