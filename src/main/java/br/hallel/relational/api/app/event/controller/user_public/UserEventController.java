package br.hallel.relational.api.app.event.controller.user_public;


import br.hallel.relational.api.app.event.dto.EventFoodTableResponseDTO;
import br.hallel.relational.api.app.event.dto.FoodSaleItemRequestDTO;
import br.hallel.relational.api.app.event.service.EventService;
import br.hallel.relational.api.app.event.service.FoodService;
import br.hallel.relational.api.app.event.service.UserEventService;
import br.hallel.relational.api.app.payment.checkout_transparent.client.MercadoPagoClient;
import br.hallel.relational.api.app.security.utils.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/user/event")
@RequiredArgsConstructor
@Tag(name = "Events - User", description = "Events part for users")
public class UserEventController {

    private final EventService eventService;
    private final MercadoPagoClient client;
    private final FoodService foodService;
    private final UserEventService userEventService;
    private final JwtTokenProvider tokenService;

    @GetMapping("/list-all/food/by-event-id/{eventId}")
    @Operation(summary = "List all foods saved in DataBase by Event Id")
    public ResponseEntity<Page<EventFoodTableResponseDTO>> listAllFoodByEventId(@PathVariable(name = "eventId")
                                                                                UUID eventId,
                                                                                Pageable pageable) {

        return ResponseEntity.ok(this.foodService.listAllFoodsByTableEventId(eventId, pageable));
    }


    @PostMapping("/generete-receipt-food/{eventId}")
    @Operation(summary = "Generate a fiscal receipt for food", description = "Handles to generate a fiscal receipt for validate buying food, return a base64 with infos")
    public ResponseEntity<String> generateFoodFiscalReceipt(@PathVariable(name = "eventId") UUID eventId, @RequestBody
    List<FoodSaleItemRequestDTO> saleItems){
        return ResponseEntity.ok(this.foodService.generateFoodFiscalReceipt(eventId, saleItems));
    }

    @GetMapping("/validate-user-frente-caixa")
    @Operation(summary = "Validate if user is frente caixa")
    public ResponseEntity<Boolean> validateIfUserIsFrenteCaixa( @RequestHeader("Authorization") String authorizationHeader, @RequestParam(name = "eventId") UUID eventId) {
        return ResponseEntity.ok(this.userEventService.validateHasFrenteCaixa(tokenService.getUserId(authorizationHeader), eventId));
    }

}
