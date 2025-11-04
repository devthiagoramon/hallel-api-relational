package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.exception.EventIllegalArumentException;
import br.hallel.relational.api.app.event.exception.EventNotFoundException;
import br.hallel.relational.api.app.event.exception.FoodNotFoundException;
import br.hallel.relational.api.app.event.model.*;
import br.hallel.relational.api.app.event.model.enum_type.StatusPaymentFood;
import br.hallel.relational.api.app.event.model.enum_type.TransactionType;
import br.hallel.relational.api.app.event.repository.*;
import br.hallel.relational.api.app.global.pdf.PdfGenerationService;
import br.hallel.relational.api.app.payment.checkout_transparent.client.MercadoPagoClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
    private final PdfGenerationService pdfGenerationService;

    public EventFoodResponseDTO createFood(FoodRequestDTO dto) {

        Foods food = new Foods();
        food.setName(dto.name());
        food.setValue(dto.value());
        food.setStockQuantity(dto.stockQuantity());
        food.setRegisteredDate(dto.registeredDate());
        Event event = getEvent(dto);
        food.setEvent(event);

        this.foodRepository.save(food);

        return new EventFoodResponseDTO().toResponse(food);
    }

    private Event getEvent(FoodRequestDTO dto) {
        return this.eventRepository.findById(dto.eventId()).orElseThrow(
                () -> new EventNotFoundException("event.id.not.found", dto.eventId().toString())
        );
    }

    public Page<EventFoodResponseDTO> listAllFoods(Pageable pageable) {

        Page<Foods> foodsPage = foodRepository.findAll(pageable);

        return foodsPage.map(EventFoodResponseDTO::toResponse);
    }


    public Page<EventFoodTableResponseDTO> listAllFoodsByTableEventId(UUID eventId, Pageable pageable) {

        Sort.Order sortOrder = pageable.getSort().stream().findFirst().orElse(null);
        if (sortOrder != null && "quantitySale".equals(sortOrder.getProperty())) {
            Pageable paginationOnly = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

            return this.foodRepository.findFoodTableByEventIdOrderBySales(eventId, paginationOnly);

        } else {
            log.info("Usando ordenação padrão do Pageable (ex: name, stockQuantity).");
            return this.foodRepository.findFoodTableByEventId(eventId, pageable);
        }
    }

    @Transactional
    public EventFoodResponseDTO getFoodById(UUID foodId) {
        Foods food = foodRepository.findById(foodId)
                .orElseThrow(() -> new FoodNotFoundException("event.food.not.found", foodId.toString()));

        return EventFoodResponseDTO.toResponse(food);
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
        log.info("Editing food sale item with id: {}", foodSaleItemId);

        FoodSaleItem saleItem = this.foodSaleItemRepository.findById(foodSaleItemId)
                .orElseThrow(() -> new RuntimeException("Venda de alimento não encontrada"));

        FoodTransaction foodTransaction = saleItem.getTransaction();
        Foods food = saleItem.getFood();

        int oldQuantity = saleItem.getQuantity();
        double oldPrice = saleItem.getPrice();
        double oldItemValue = oldPrice * oldQuantity;

        int newQuantity = dto.quantity();
        double newPrice = dto.price();
        double newItemValue = newPrice * newQuantity;

        int quantityDiff = newQuantity - oldQuantity;
        food.setStockQuantity(food.getStockQuantity() - quantityDiff);
        this.foodRepository.save(food);

        saleItem.setQuantity(newQuantity);
        saleItem.setPrice(newPrice);
        this.foodSaleItemRepository.save(saleItem);

        double oldTransactionTotal = foodTransaction.getValue();
        double newTransactionTotal = oldTransactionTotal - oldItemValue + newItemValue;

        foodTransaction.setValue(newTransactionTotal);
        this.foodTransactionRepository.save(foodTransaction);

        EventTransaction eventTransaction = foodTransaction.getEventTransaction();
        if (eventTransaction != null) {
            eventTransaction.setValue(newTransactionTotal);
            this.eventTransactionRepository.save(eventTransaction);
        }

        return EventFoodSoldResponseDTO.toResponseDTO(saleItem);
    }

    public EventFoodResponseDTO editFood(UUID foodId, FoodEditDTO dto) {
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
        return new EventFoodResponseDTO().toResponse(updatedFood);
    }

    public void deleteFood(UUID foodId) {
        log.info("Deleting food by id... " + foodId.toString());
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
        foodTransaction.setDateTransaction(OffsetDateTime.now(ZoneId.of("America/Manaus")));
        foodTransaction.setValue(0.0);
        FoodTransaction savedTransaction1 = foodTransactionRepository.save(foodTransaction);

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
            saleItem.setTransaction(savedTransaction1);
            saleItem.setFood(food);
            saleItem.setQuantity(itemDTO.quantity());
            saleItem.setPrice(itemDTO.price().doubleValue());
            saleItem.setEvent(eventRepository.findById(eventId).get());
            saleItem.setSoldAt(LocalDateTime.now());

            itemsToSave.add(saleItem);
        }

        String finalDescription = String.join(", ", foodDescriptions);

        savedTransaction1.setDescription(finalDescription);
        savedTransaction1.setValue(totalAmount.doubleValue());
        FoodTransaction savedTransaction2 = foodTransactionRepository.save(savedTransaction1);
        foodSaleItemRepository.saveAll(itemsToSave);

        try {
            Payment payment = mercadoPagoClient.createFoodPixPayment(totalAmount, finalDescription, foodTransaction.getId());
            savedTransaction2.setMercadoPagoPaymentId(payment.getId());
            foodTransactionRepository.save(savedTransaction2);

            String pixCode = payment.getPointOfInteraction().getTransactionData().getQrCode();
            String qrCodeBase64 = payment.getPointOfInteraction().getTransactionData().getQrCodeBase64();

            return new PaymentFoodResponseDTO(pixCode, qrCodeBase64, foodTransaction.getId());

        } catch (MPApiException | MPException e) {
            log.error("Failed to create food payment: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing payment with Mercado Pago.", e);
        }
    }

    public String generateFoodFiscalReceipt(UUID eventId, List<FoodSaleItemRequestDTO> saleItems) {
        FoodTransaction foodTransaction = new FoodTransaction();
        foodTransaction.setStatus(StatusPaymentFood.PENDENTE);
        foodTransaction.setEvent(eventRepository.findById(eventId).orElseThrow());
        foodTransaction.setDateTransaction(OffsetDateTime.now(ZoneId.of("America/Manaus")));
        foodTransaction.setValue(0.0);
        FoodTransaction savedTransaction1 = foodTransactionRepository.save(foodTransaction);

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
            saleItem.setTransaction(savedTransaction1);
            saleItem.setFood(food);
            saleItem.setQuantity(itemDTO.quantity());
            saleItem.setPrice(itemDTO.price().doubleValue());
            saleItem.setEvent(eventRepository.findById(eventId).get());
            saleItem.setSoldAt(LocalDateTime.now());

            itemsToSave.add(saleItem);
        }

        String finalDescription = String.join(", ", foodDescriptions);

        savedTransaction1.setDescription(finalDescription);
        savedTransaction1.setValue(totalAmount.doubleValue());
        FoodTransaction savedTransaction2 = foodTransactionRepository.save(savedTransaction1);
        List<FoodSaleItem> savedItems = foodSaleItemRepository.saveAll(itemsToSave);

        EventTransaction eventTransaction = new EventTransaction();
        eventTransaction.setDescription("Venda de Alimento Confirmada: " + savedTransaction2.getDescription());
        eventTransaction.setValue(savedTransaction2.getValue());
        eventTransaction.setTransactionType(TransactionType.ENTRADA);
        eventTransaction.setEvent(savedTransaction2.getEvent());
        eventTransaction.setDateTransaction(new Date());
        EventTransaction eventTransaction1 = eventTransactionRepository.save(eventTransaction);
        savedTransaction2.setEventTransaction(eventTransaction1);

        for (FoodSaleItem item : savedItems) {
            Foods food = item.getFood();
            if (food != null) {
                int newStock = food.getStockQuantity() - item.getQuantity();
                food.setStockQuantity(newStock);
                foodRepository.save(food);
            }
        }
        savedTransaction2.setStatus(StatusPaymentFood.PAGO);
        savedTransaction2.setDateTransaction(OffsetDateTime.now(ZoneId.of("America/Manaus")));
        savedTransaction2.setSaleItems(savedItems);
        FoodTransaction savedTransaction3 = foodTransactionRepository.save(savedTransaction2);
        log.info("EVENT TRANSACTION CRIADO " + eventTransaction1.getId().toString());
        return pdfGenerationService.gerarComandaAlimentoBase64(savedTransaction3);
    }
}
