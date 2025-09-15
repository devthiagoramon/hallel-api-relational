package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.exception.EventIllegalArumentException;
import br.hallel.relational.api.app.event.exception.EventNotFoundException;
import br.hallel.relational.api.app.event.exception.FoodNotFoundException;
import br.hallel.relational.api.app.event.model.*;
import br.hallel.relational.api.app.event.repository.*;
import br.hallel.relational.api.app.payment.checkout_transparent.client.MercadoPagoClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;
    private final EventRepository eventRepository;
    private final EventTransactionRepository eventTransactionRepository;
    private final FoodSaleItemRepository foodSaleItemRepository;
    private final MercadoPagoClient mercadoPagoClient;
    private final FoodTransactionRepository foodTransactionRepository;
    private final SimpMessagingTemplate template;

    public FoodResponseDTO createFood(FoodRequestDTO dto) {

        Foods food = new Foods();
        food.setName(dto.name());
        food.setValue(dto.value());
        food.setStockQuantity(dto.stockQuantity());
        food.setRegisteredDate(dto.registeredDate());
        Event event = getEvent(dto);
        food.setEvent(event);

        this.foodRepository.save(food);

        return new FoodResponseDTO().toResponse(food);
    }

    private Event getEvent(FoodRequestDTO dto) {
        return this.eventRepository.findById(dto.eventId()).orElseThrow(
                () -> new EventNotFoundException("event.id.not.found", dto.eventId().toString())
        );
    }

    public Page<FoodResponseDTO> listAllFoods(Pageable pageable) {

        Page<Foods> foodsPage = foodRepository.findAll(pageable);

        return foodsPage.map(FoodResponseDTO::toResponse);
    }


    public Page<FoodResponseDTO> listAllFoodsByEventId(UUID eventId, Pageable pageable) {
        Page<Foods> foodsPage = foodRepository.findAllByEvent_Id(eventId, pageable);

        return foodsPage.map(FoodResponseDTO::toResponse);
    }


    @Transactional
    public FoodResponseDTO getFoodById(UUID foodId) {
        Foods food = foodRepository.findById(foodId)
                .orElseThrow(() -> new FoodNotFoundException("event.food.not.found", foodId.toString()));

        return FoodResponseDTO.toResponse(food);
    }

    @Transactional
    public EventTransactionResponse registerSale(List<FoodSaleItemRequestDTO> foodSaleItems) {
        if (foodSaleItems == null || foodSaleItems.isEmpty()) {
            throw new IllegalArgumentException("Lista de alimentos para venda não pode estar vazia.");
        }

        EventTransaction transaction = new EventTransaction();
        transaction.setTransactionType(TransactionType.ENTRADA);

        double totalValue = 0.0;
        Event event = null;

        List<String> foodDescriptions = new ArrayList<>();

        for (FoodSaleItemRequestDTO item : foodSaleItems) {
            Foods food = this.foodRepository.findById(item.foodId())
                    .orElseThrow(() -> new FoodNotFoundException("food.event.not.found", item.foodId().toString()));
            if (event != null) {
                event = food.getEvent();
            }

            if (food.getStockQuantity() < item.quantity()) {
                throw new EventIllegalArumentException("A quantidade de Alimentos para venda é maior que a quantidade em estoque!");
            }

            totalValue += food.getValue() * item.quantity();

            FoodSaleItem sale = new FoodSaleItem();
            sale.setFood(food);
            sale.setEvent(food.getEvent());
            sale.setQuantity(item.quantity());
            sale.setPrice(food.getValue());
            this.foodSaleItemRepository.save(sale);

            food.setStockQuantity(food.getStockQuantity() - item.quantity());
            this.foodRepository.save(food);
            foodDescriptions.add(food.getName() + " (x" + item.quantity() + ")");
        }

        transaction.setValue(totalValue);
        transaction.setDescription("Venda: " + String.join(", ", foodDescriptions));
        transaction.setIsEditable(Boolean.FALSE);
        transaction.setEvent(event);
        this.eventTransactionRepository.save(transaction);

        return EventTransactionResponse.toResponse(transaction);
    }

    public Page<EventFoodSoldResponseDTO> listAllFoodsSoldByEventId(UUID eventId, Pageable pageable) {
        log.info("Listing all food by event id:  " + eventId.toString());

        Page<FoodSaleItem> foods = this.foodSaleItemRepository.findAllByEvent_IdAndTransaction_Status(eventId,
                StatusPaymentFood.PAGO, pageable);

        return foods.map(foodObject -> new EventFoodSoldResponseDTO().toResponseDTO(foodObject));
    }

    public EventFoodSoldResponseDTO getFoodSoldById(UUID eventFoodSoldId) {
        log.info("Get food Sold by event id:  " + eventFoodSoldId.toString());
        FoodSaleItem foods = this.foodSaleItemRepository.findById(eventFoodSoldId).orElseThrow(
                () -> new FoodNotFoundException("food.event.not.found", eventFoodSoldId.toString())
        );
        return new EventFoodSoldResponseDTO().toResponseDTO(foods);
    }

    @Transactional
    public void deleteFoodSold(UUID foodSaleItemId) {
        log.info("Deleting food sale item with id: " + foodSaleItemId.toString());

        FoodSaleItem saleItem = this.foodSaleItemRepository.findById(foodSaleItemId)
                .orElseThrow(() -> new RuntimeException("Item de venda não encontrado"));

        FoodTransaction foodTransaction = saleItem.getTransaction();
        EventTransaction eventTransaction = foodTransaction.getEventTransaction();
        Foods food = saleItem.getFood();

        food.setStockQuantity(food.getStockQuantity() + saleItem.getQuantity());
        this.foodRepository.save(food);

        double itemValue = saleItem.getPrice() * saleItem.getQuantity();
        foodTransaction.setValue(foodTransaction.getValue() - itemValue);
        eventTransaction.setValue(eventTransaction.getValue() - itemValue);

        this.foodTransactionRepository.save(foodTransaction);
        this.eventTransactionRepository.save(eventTransaction);

        this.foodSaleItemRepository.delete(saleItem);

        if (foodTransaction.getValue() <= 0) {
            this.foodTransactionRepository.delete(foodTransaction);
        }
    }

    @Transactional
    public EventFoodSoldResponseDTO editFoodSold(UUID foodSaleItemId, EventFoodSaleDTO dto) {
        log.info("Editing food sale item with id: " + foodSaleItemId.toString());

        FoodSaleItem saleItem = this.foodSaleItemRepository.findById(foodSaleItemId)
                .orElseThrow(() -> new RuntimeException("Venda de alimento não encontrada"));

        FoodTransaction foodTransaction = saleItem.getTransaction();
        EventTransaction eventTransaction = foodTransaction.getEventTransaction();
        Foods food = saleItem.getFood();

        int oldQuantity = saleItem.getQuantity();
        int newQuantity = dto.quantity();
        int quantityDiff = newQuantity - oldQuantity;
        food.setStockQuantity(food.getStockQuantity() - quantityDiff);
        this.foodRepository.save(food);

        saleItem.setQuantity(newQuantity);
        saleItem.setPrice(dto.price());
        this.foodSaleItemRepository.save(saleItem);

        double total = foodTransaction.getSaleItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        foodTransaction.setValue(total);
        eventTransaction.setValue(total);
        this.foodTransactionRepository.save(foodTransaction);
        this.eventTransactionRepository.save(eventTransaction);

        return EventFoodSoldResponseDTO.toResponseDTO(saleItem);
    }

    public FoodResponseDTO editFood(UUID foodId, FoodEditDTO dto) {
        Foods food = this.foodRepository.findById(foodId).orElseThrow(
                () -> new FoodNotFoundException("food.event.not.found", foodId.toString())
        );
        if (dto.name() != null) {
            food.setName(dto.name());
        }
        if (dto.value() != null) {
            food.setValue(dto.value());
        }

        if (dto.stockQuantity() != null) {
            food.setStockQuantity(dto.stockQuantity());
        }

        if (dto.eventId() != null) {
            food.setEvent(
                    this.eventRepository.findById(dto.eventId()).orElseThrow(
                            () -> new EventNotFoundException("event.event.not.found", dto.eventId().toString())
                    )
            );
        }

        Foods updatedFood = this.foodRepository.save(food);
        return new FoodResponseDTO().toResponse(updatedFood);
    }

    public void deleteFood(UUID foodId) {
        Foods foods = this.foodRepository.findById(foodId).orElseThrow(
                () -> new FoodNotFoundException("food.event.not.found", foodId.toString())
        );
        this.foodRepository.delete(foods);
    }

    @Transactional
    public PaymentFoodResponseDTO createFoodPayment(List<FoodSaleItemRequestDTO> saleItems, UUID eventId) {
        log.info("Creating payment for food sale items: {}", saleItems);

        FoodTransaction foodTransaction = new FoodTransaction();
        foodTransaction.setStatus(StatusPaymentFood.PENDENTE);
        foodTransaction.setEvent(eventRepository.findById(eventId).orElseThrow());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<FoodSaleItem> itemsToSave = new ArrayList<>();

        List<String> foodDescriptions = new ArrayList<>();

        for (FoodSaleItemRequestDTO itemDTO : saleItems) {
            Foods food = foodRepository.findById(itemDTO.foodId())
                    .orElseThrow(() -> new FoodNotFoundException("Food not found", itemDTO.foodId().toString()));

            if (food.getStockQuantity() < itemDTO.quantity()) {
                throw new EventIllegalArumentException("Insufficient stock for food: " + food.getName());
            }

            totalAmount = totalAmount.add(itemDTO.price().multiply(BigDecimal.valueOf(itemDTO.quantity())));

            foodDescriptions.add(itemDTO.quantity() + "x " + food.getName());

            FoodSaleItem saleItem = new FoodSaleItem();
            saleItem.setTransaction(foodTransaction);
            saleItem.setFood(food);
            saleItem.setQuantity(itemDTO.quantity());
            saleItem.setPrice(itemDTO.price().doubleValue());
            saleItem.setEvent(eventRepository.findById(eventId).get());
            saleItem.setSoldAt(LocalDateTime.now());

            itemsToSave.add(saleItem);
        }

        String finalDescription = String.join(", ", foodDescriptions);

        foodTransaction.setDescription(finalDescription);
        foodTransaction.setValue(totalAmount.doubleValue());
        foodTransaction.setDateTransaction(OffsetDateTime.now(ZoneId.of("America/Manaus")));

        foodTransactionRepository.save(foodTransaction);
        foodSaleItemRepository.saveAll(itemsToSave);

        try {
            Payment payment = mercadoPagoClient.createFoodPixPayment(totalAmount, finalDescription, foodTransaction.getId());
            foodTransaction.setMercadoPagoPaymentId(payment.getId());
            foodTransactionRepository.save(foodTransaction);

            String pixCode = payment.getPointOfInteraction().getTransactionData().getQrCode();
            String qrCodeBase64 = payment.getPointOfInteraction().getTransactionData().getQrCodeBase64();

            return new PaymentFoodResponseDTO(pixCode, qrCodeBase64, foodTransaction.getId());

        } catch (MPApiException | MPException e) {
            log.error("Failed to create food payment: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing payment with Mercado Pago.", e);
        }
    }
}
