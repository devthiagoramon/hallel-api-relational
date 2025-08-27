package br.hallel.relational.api.app.event.controller.admin;

import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.model.TransactionType;
import br.hallel.relational.api.app.event.service.EventService;
import br.hallel.relational.api.app.payment.checkout_transparent.client.MercadoPagoClient;
import br.hallel.relational.api.app.payment.checkout_transparent.dto.CreatePixPaymentRequestDTO;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/event")
@RequiredArgsConstructor
@Tag(name = "Admin Event", description = "Admin part for event managment")
public class AdminEventController {

    private final EventService eventService;
    private final MercadoPagoClient client;

    //** CRIANDO EVENTO **
    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<EventResponse> createEvent(@RequestPart(name = "request") EventDTO eventDTO,
                                                     @RequestPart(name = "image_url") MultipartFile img_url,
                                                     @RequestPart(name = "banner_url") MultipartFile banner_url) {
        return ResponseEntity.ok(eventService.create(eventDTO, img_url, banner_url));
    }

    @PatchMapping(value = "/edit/{id}", consumes = "multipart/form-data")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable(name = "id") UUID id,
                                                     @RequestPart(name = "request") EventDTO eventDTO,
                                                     @RequestPart(name = "image_url", required = false) MultipartFile img_url,
                                                     @RequestPart(name = "banner_url", required = false) MultipartFile banner_url) {
        return ResponseEntity.ok(this.eventService.updateById(id, eventDTO, img_url, banner_url));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteEvent(@PathVariable(name = "id") UUID id) {
        this.eventService.getEventById(id);
        boolean deleted = this.eventService.deleteById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", deleted);
        response.put("message", deleted
                ? "Evento deletado com sucesso."
                : "Evento não encontrado ou não pôde ser deletado.");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create/transaction")
    public ResponseEntity<EventTransactionResponse> create(@RequestBody EventTransactionDTO dto) {
        return ResponseEntity.ok(eventService.addTransaction(dto));
    }

    @GetMapping("/transaction/list-all")
    public ResponseEntity<List<EventTransactionResponse>> listAllTransaction() {
        return ResponseEntity.ok(eventService.listAllTransactions());
    }

   @GetMapping("/transaction/get/{transactionId}")
    public ResponseEntity<EventTransactionResponse> getTransactionById(@PathVariable(name = "transactionId") UUID transactionId) {
        return ResponseEntity.ok(eventService.getTransactionById(transactionId));
    }

    @GetMapping("/transaction/list-all/by-event/{eventId}")
    public ResponseEntity<List<EventTransactionResponse>> listAllTransactionsByEvent(@PathVariable UUID eventId) {
        return ResponseEntity.ok(eventService.listAllTransactionsByEvent(eventId));
    }

    @GetMapping("/transaction-type/list-all/by-event/{eventId}")
    @Operation(summary = "List all transactions by event ID and Transaction Type (ENTRADA or SAIDA)")
    public ResponseEntity<List<EventTransactionResponse>> listAllTransactionsByEventAndTransactionType(@PathVariable UUID eventId,
                                                                                                       @RequestParam TransactionType type) {
        return ResponseEntity.ok(eventService.listAllTransactionsByEventAndTransactionType(eventId, type));
    }

    @GetMapping("/transaction/by-id/{eventId}")
    public ResponseEntity<EventTransactionResponse> findTransactionById(@PathVariable UUID eventId) {
        return ResponseEntity.ok(eventService.findTransactionById(eventId));
    }

    @PutMapping("/transaction/edit/{id}")
    public ResponseEntity<EventTransactionResponse> updateTransaction(@PathVariable UUID id, @RequestBody EventTransactionDTO dto) {
        return ResponseEntity.ok(eventService.updateTransaction(id, dto));
    }

    @DeleteMapping("/transaction/delete/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id) {
        eventService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create-pix")
    @Operation(summary = "Create an Pix Charge" +
            "needs the information of the customer who will make the payment")
    public ResponseEntity<Map<String, String>> createPixPayment(@Valid @RequestBody CreatePixPaymentRequestDTO requestDTO) {
        log.info("Creating a Pix payment...");
        try {
            Payment payment = client.createPixPayment(requestDTO);

            String pixCode = payment.getPointOfInteraction().getTransactionData().getQrCode();
            String qrCodeBase64 = payment.getPointOfInteraction().getTransactionData().getQrCodeBase64();

            return ResponseEntity.ok(Map.of(
                    "pixCode", pixCode,
                    "qrCodeBase64", qrCodeBase64
            ));
        } catch (MPApiException apiException) {
            String apiResponseDetails = apiException.getApiResponse().getContent();
            int statusCode = apiException.getStatusCode();

            log.error("Erro na API do Mercado Pago. Status: {}, Mensagem: {}. Detalhes da resposta: {}",
                    statusCode,
                    apiException.getMessage(),
                    apiResponseDetails);

            return ResponseEntity.status(statusCode).body(Map.of(
                    "error", "Erro da API do Mercado Pago.",
                    "details", apiResponseDetails
            ));
        } catch (MPException e) {
            log.error("Erro inesperado na SDK do Mercado Pago", e);
            return ResponseEntity.status(500).body(Map.of("error", "Erro inesperado ao criar pagamento Pix."));
        }
    }

    @GetMapping("/get-balance/{eventId}")
    @Operation(summary = "Add participation to event", description = "Handles the action of admin add new participants")
    public ResponseEntity<EventBalanceResponse> getInputAndOutputBalance(
            @PathVariable(name = "eventId") UUID eventId
    ) {
        return ResponseEntity.ok(this.eventService.getBalance(eventId));
    }
}
