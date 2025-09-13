package br.hallel.relational.api.app.event.controller.admin;

import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.exception.EventParticipationException;
import br.hallel.relational.api.app.event.model.EventFoodSales;
import br.hallel.relational.api.app.event.model.EventParticipation;
import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.model.TransactionType;
import br.hallel.relational.api.app.event.repository.EventParticipationRepository;
import br.hallel.relational.api.app.event.service.EventService;
import br.hallel.relational.api.app.event.service.FoodService;
import br.hallel.relational.api.app.payment.checkout_transparent.client.MercadoPagoClient;
import br.hallel.relational.api.app.payment.checkout_transparent.exceptions.MercadoPagoAPIException;
import br.hallel.relational.api.app.payment.checkout_transparent.exceptions.MercadoPagoException;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate template;
    private final EventParticipationRepository eventParticipationRepository;
    private final FoodService foodService;


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
                                                     @RequestPart(name = "image_url", required = false)
                                                     MultipartFile img_url,
                                                     @RequestPart(name = "banner_url", required = false)
                                                     MultipartFile banner_url) {
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
    public ResponseEntity<EventTransactionResponse> getTransactionById(
            @PathVariable(name = "transactionId") UUID transactionId) {
        return ResponseEntity.ok(eventService.getTransactionById(transactionId));
    }

    @GetMapping("/transaction/list-all/by-event/{eventId}")
    public ResponseEntity<Page<EventTransactionResponse>> listAllTransactionsByEvent(@PathVariable UUID eventId,
                                                                                     @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return ResponseEntity.ok(eventService.listAllTransactionsByEvent(eventId, PageRequest.of(page, size)));
    }

    @GetMapping("/transaction-type/list-all/by-event/{eventId}")
    @Operation(summary = "List all transactions by event ID and Transaction Type (ENTRADA or SAIDA)")
    public ResponseEntity<Page<EventTransactionResponse>> listAllTransactionsByEventAndTransactionType(
            @PathVariable UUID eventId,
            @RequestParam TransactionType type, @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return ResponseEntity.ok(
                eventService.listAllTransactionsByEventAndTransactionType(eventId, type, PageRequest.of(page, size)));
    }

    @GetMapping("/transaction/by-id/{eventId}")
    public ResponseEntity<EventTransactionResponse> findTransactionById(@PathVariable UUID eventId) {
        return ResponseEntity.ok(eventService.findTransactionById(eventId));
    }

    @PutMapping("/transaction/edit/{id}")
    public ResponseEntity<EventTransactionResponse> updateTransaction(@PathVariable UUID id,
                                                                      @RequestBody EventTransactionDTO dto) {
        return ResponseEntity.ok(eventService.updateTransaction(id, dto));
    }

    @DeleteMapping("/transaction/delete/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id) {
        eventService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get-payment-pix-of-user")
    @Operation(summary = "Get the Pix created when participant accept participation in event")
    public ResponseEntity<Map<String, String>> createPixPayment(@RequestParam(name = "eventId") UUID eventId,
                                                                @RequestParam(name = "userId") UUID userId) {
        log.info("Creating a Pix payment...");
        try {
            EventParticipation participation = this.eventParticipationRepository.findByUser_IdAndEvent_Id(userId,
                            eventId)
                    .orElseThrow(() -> new EventParticipationException("participation.event.not.found"));
            String qrCode = client.getPaymentQRCode(participation.getMercadoPagoPaymentId());
            String pixCode = client.getPaymentPixCode(participation.getMercadoPagoPaymentId());


            template.convertAndSend("/topic/payments/" + userId,
                    new PaymentStatusDTO(qrCode, pixCode,
                            StatusPaymentEventParticipation.PENDENTE));
            return ResponseEntity.ok(Map.of(
                    "pixCode", pixCode,
                    "qrCodeBase64", qrCode
            ));
        } catch (MPApiException apiException) {
            String apiResponseDetails = apiException.getApiResponse().getContent();
            int statusCode = apiException.getStatusCode();

            log.error("Erro na API do Mercado Pago. Status: {}, Mensagem: {}. Detalhes da resposta: {}",
                    statusCode,
                    apiException.getMessage(),
                    apiResponseDetails);

            throw new MercadoPagoAPIException(
                    "Não foi possivel conectar com a API do mercado pago: " + apiResponseDetails);
        } catch (MPException e) {
            log.error("Erro inesperado na SDK do Mercado Pago", e);
            throw new MercadoPagoException("Ocorreu um erro com o cliente do mercado pago: " + e.getMessage());
        }
    }

    @GetMapping("/get-balance/{eventId}")
    @Operation(summary = "Get the balance of event", description = "Handles the action of getting the balance of event")
    public ResponseEntity<EventBalanceResponse> getInputAndOutputBalance(
            @PathVariable(name = "eventId") UUID eventId
    ) {
        return ResponseEntity.ok(this.eventService.getBalance(eventId));
    }

    @GetMapping("/get-cash-flow/{eventId}")
    @Operation(summary = "Get the balance of event", description = "Handles the action of getting the balance of event")
    public ResponseEntity<List<EventCashFlowResponse>> getCashFlow(
            @PathVariable(name = "eventId") UUID eventId
    ) {
        return ResponseEntity.ok(this.eventService.getEventCashFlow(eventId));
    }

    //FOODS
    @PostMapping("/create/food")
    @Operation(summary = "Create and Save and food in DataBase")
    public ResponseEntity<FoodResponseDTO> createFood(@RequestBody @Valid FoodRequestDTO dto) {
        return ResponseEntity.ok(this.foodService.createFood(dto));
    }

    @GetMapping("/list-all/foods")
    @Operation(summary = "List all foods saved in DataBase")
    public ResponseEntity<Page<FoodResponseDTO>> listAllFood(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(this.foodService.listAllFoods(PageRequest.of(page, size)));
    }

    @GetMapping("/list-all/food/by-event-id/{eventId}")
    @Operation(summary = "List all foods saved in DataBase by Event Id")
    public ResponseEntity<Page<FoodResponseDTO>> listAllFoodByEventId(@PathVariable(name = "eventId") UUID eventId,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(this.foodService.listAllFoodsByEventId(eventId, PageRequest.of(page, size)));
    }

    @GetMapping("/get-by-id/food/{foodId}")
    @Operation(summary = "Getting Food By Id")
    public ResponseEntity<FoodResponseDTO> getFoodById(@PathVariable(name = "foodId") UUID foodId) {
        return ResponseEntity.ok(this.foodService.getFoodById(foodId));
    }

    @PatchMapping("/edit-by-id/food/{foodId}")
    @Operation(summary = "Edit Food By Id")
    public ResponseEntity<FoodResponseDTO> editFoodById(@PathVariable(name = "foodId") UUID foodId,
                                                        @RequestBody FoodEditDTO dto) {
        return ResponseEntity.ok(this.foodService.editFood(foodId, dto));
    }

    @DeleteMapping("/delete-by-id/food/{foodId}")
    @Operation(summary = "Delete Food By Id")
    public ResponseEntity<?> deleteFoodById(@PathVariable(name = "foodId") UUID foodId) {
        this.foodService.deleteFood(foodId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register-sale/food")
    @Operation(summary = "Record a food sale")
    public ResponseEntity<EventTransactionResponse> registerSale(@RequestParam(name = "foodId") UUID foodId,
                                                                 @RequestParam(name = "quantity") Integer quantity) {
        return ResponseEntity.ok(this.foodService.registerSale(foodId, quantity));
    }

    @GetMapping("/list-all/solds/foods/{eventId}")
    @Operation(summary = "List All foods sold")
    public ResponseEntity<Page<EventFoodSoldResponseDTO>> listAllFoodsSold(
            @PathVariable(name = "eventId") UUID eventId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        return ResponseEntity.ok(this.foodService.listAllFoodsSoldByEventId(eventId, PageRequest.of(page, size)));
    }

    @GetMapping("/get/food-sold/{eventId}")
    @Operation(summary = "List All foods sold")
    public ResponseEntity<EventFoodSoldResponseDTO> getEventFoodSold(
            @PathVariable(name = "eventId") UUID eventId) {

        return ResponseEntity.ok(this.foodService.getFoodSoldById(eventId));
    }

    @DeleteMapping("/delete/food-sold/{eventFoodSoldId}")
    @Operation(summary = "Delete foods sold By Id")
    public ResponseEntity<?> deleteFoodSoldById(@PathVariable(name = "eventFoodSoldId") UUID eventFoodSoldId) {
        this.foodService.deleteFoodSold(eventFoodSoldId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/edit/food-sold/{eventFoodSoldId}")
    @Operation(summary = "edit foods sold By Id")
    public ResponseEntity<EventFoodSales> editFoodSale(
            @PathVariable("eventFoodSoldId") UUID eventFoodSoldId,
            @RequestBody EventFoodSaleDTO dto
    ) {
        return ResponseEntity.ok(this.foodService.edit(eventFoodSoldId, dto));
    }
}
