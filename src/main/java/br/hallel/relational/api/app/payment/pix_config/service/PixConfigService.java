package br.hallel.relational.api.app.payment.pix_config.service;

import br.hallel.relational.api.app.payment.pix_config.dto.CreatePixConfigRequestDTO;
import br.hallel.relational.api.app.payment.pix_config.dto.PixConfigResponseDTO;
import br.hallel.relational.api.app.payment.pix_config.dto.UpdatePixConfigRequestDTO;
import br.hallel.relational.api.app.payment.pix_config.exception.PixConfigNotFoundException;
import br.hallel.relational.api.app.payment.pix_config.model.PixConfig;
import br.hallel.relational.api.app.payment.pix_config.repository.PixConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PixConfigService {

    private final PixConfigRepository pixConfigRepository;

    public List<PixConfigResponseDTO> listAll() {
        return pixConfigRepository.findAll()
                .stream()
                .map(PixConfigResponseDTO::toResponse)
                .toList();
    }

    public PixConfigResponseDTO getById(UUID id) {
        PixConfig pixConfig = pixConfigRepository.findById(id)
                .orElseThrow(() -> new PixConfigNotFoundException("Configuração PIX não encontrada: " + id));
        return PixConfigResponseDTO.toResponse(pixConfig);
    }

    public PixConfigResponseDTO getActive() {
        return pixConfigRepository.findByAtivoTrue()
                .map(PixConfigResponseDTO::toResponse)
                .orElseThrow(() -> new PixConfigNotFoundException("Nenhuma configuração PIX ativa encontrada"));
    }

    @Transactional
    public PixConfigResponseDTO create(CreatePixConfigRequestDTO dto) {
        PixConfig pixConfig = new PixConfig();
        pixConfig.setChavePix(dto.chavePix());
        pixConfig.setTipoChave(dto.tipoChave());
        pixConfig.setNomeBanco(dto.nomeBanco());
        pixConfig.setNomeRecebedor(dto.nomeRecebedor());
        pixConfig.setDescricao(dto.descricao());
        pixConfig.setMercadoPagoAccessToken(dto.mercadoPagoAccessToken());
        pixConfig.setAtivo(false);

        PixConfig saved = pixConfigRepository.save(pixConfig);
        log.info("PIX config criada com id {}", saved.getId());
        return PixConfigResponseDTO.toResponse(saved);
    }

    @Transactional
    public PixConfigResponseDTO update(UUID id, UpdatePixConfigRequestDTO dto) {
        PixConfig pixConfig = pixConfigRepository.findById(id)
                .orElseThrow(() -> new PixConfigNotFoundException("Configuração PIX não encontrada: " + id));

        pixConfig.setChavePix(dto.chavePix());
        pixConfig.setTipoChave(dto.tipoChave());
        pixConfig.setNomeBanco(dto.nomeBanco());
        pixConfig.setNomeRecebedor(dto.nomeRecebedor());
        pixConfig.setDescricao(dto.descricao());

        // Só atualiza o token se um novo valor foi fornecido
        if (dto.mercadoPagoAccessToken() != null && !dto.mercadoPagoAccessToken().isBlank()) {
            pixConfig.setMercadoPagoAccessToken(dto.mercadoPagoAccessToken());
        }

        PixConfig saved = pixConfigRepository.save(pixConfig);
        log.info("PIX config atualizada com id {}", saved.getId());
        return PixConfigResponseDTO.toResponse(saved);
    }

    @Transactional
    public PixConfigResponseDTO activate(UUID id) {
        PixConfig pixConfig = pixConfigRepository.findById(id)
                .orElseThrow(() -> new PixConfigNotFoundException("Configuração PIX não encontrada: " + id));

        pixConfigRepository.deactivateAll();
        pixConfig.setAtivo(true);

        PixConfig saved = pixConfigRepository.save(pixConfig);
        log.info("PIX config ativada: id {}", saved.getId());
        return PixConfigResponseDTO.toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        PixConfig pixConfig = pixConfigRepository.findById(id)
                .orElseThrow(() -> new PixConfigNotFoundException("Configuração PIX não encontrada: " + id));

        if (pixConfig.isAtivo()) {
            throw new IllegalStateException(
                    "Não é possível excluir a configuração PIX ativa. Ative outra configuração antes de excluir esta.");
        }

        pixConfigRepository.delete(pixConfig);
        log.info("PIX config excluída: id {}", id);
    }
}
