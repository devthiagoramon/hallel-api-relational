package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.event.dto.*;
import br.hallel.relational.api.app.event.exception.EventIllegalArumentException;
import br.hallel.relational.api.app.event.exception.EventNotFoundException;
import br.hallel.relational.api.app.event.exception.FoodNotFoundException;
import br.hallel.relational.api.app.event.model.*;
import br.hallel.relational.api.app.event.repository.EventFoodSaleRepository;
import br.hallel.relational.api.app.event.repository.EventRepository;
import br.hallel.relational.api.app.event.repository.EventTransactionRepository;
import br.hallel.relational.api.app.event.repository.FoodRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;
    private final EventRepository eventRepository;
    private final EventTransactionRepository eventTransactionRepository;
    private final EventFoodSaleRepository eventFoodSaleRepository;

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

    public EventTransactionResponse registerSale(UUID foodId, Integer quantity) {

        Foods food = this.foodRepository.findById(foodId).orElseThrow(
                () -> new FoodNotFoundException("food.event.not.found", foodId.toString())
        );

        if (food.getStockQuantity() < quantity) {
            throw new EventIllegalArumentException("A quantidade de Alimentos para venda é maior que a quantidade em estoque!");
        }

        BigDecimal totalValue = food.getValue().multiply(new BigDecimal(quantity));

        EventTransaction transaction = new EventTransaction();
        transaction.setValue(totalValue.doubleValue());
        transaction.setDesciption("Venda: %s (x%d)".formatted(food.getName(), quantity));
        transaction.setIsEditable(Boolean.FALSE);
        transaction.setTransactionType(TransactionType.ENTRADA);
        transaction.setEvent(food.getEvent());
        this.eventTransactionRepository.save(transaction);


        EventFoodSales sale = new EventFoodSales();
        sale.setFood(food);
        sale.setEvent(food.getEvent());
        sale.setQuantity(quantity);
        sale.setPrice(totalValue);
        this.eventFoodSaleRepository.save(sale);

        food.setStockQuantity(food.getStockQuantity() - quantity);
        this.foodRepository.save(food);

        return EventTransactionResponse.toResponse(transaction);
    }

    public Page<EventFoodSoldResponseDTO> listAllFoodsSoldByEventId(UUID eventId, Pageable pageable) {
        log.info("Listing all food by event id:  " + eventId.toString());
        Page<EventFoodSales> foods = this.eventFoodSaleRepository.findAllByEvent_Id(eventId, pageable);
        return foods.map(foodObject -> new EventFoodSoldResponseDTO().toResponseDTO(foodObject));
    }

    public EventFoodSoldResponseDTO getFoodSoldById(UUID eventFoodSoldId) {
        log.info("Get food by event id:  " + eventFoodSoldId.toString());
        EventFoodSales foods = this.eventFoodSaleRepository.findById(eventFoodSoldId).get();
        return new EventFoodSoldResponseDTO().toResponseDTO(foods);
    }

    public void deleteFoodSold(UUID eventFoodSoldId) {
        log.info("Deleting food sold by event id:  " + eventFoodSoldId.toString());
        Optional<EventFoodSales> optional = this.eventFoodSaleRepository.findById(eventFoodSoldId);
        if (optional.isPresent()) {
            this.eventTransactionRepository.deleteById(optional.get().getEventTransaction().getId());
            this.eventFoodSaleRepository.deleteById(eventFoodSoldId);
        }
    }

    @Transactional
    public EventFoodSales edit(UUID eventFoodSaleId, EventFoodSaleDTO dto) {

        EventFoodSales sale = this.eventFoodSaleRepository.findById(eventFoodSaleId)
                .orElseThrow(() -> new RuntimeException("Venda de alimento não encontrada"));

        Foods food = sale.getFood();
        EventTransaction transaction = sale.getEventTransaction();

        int oldQuantity = sale.getQuantity();
        int newQuantity = dto.quantity();
        int quantityDiff = newQuantity - oldQuantity;

        food.setStockQuantity(food.getStockQuantity() - quantityDiff);
        this.foodRepository.save(food);


        sale.setQuantity(newQuantity);
        sale.setPrice(dto.price());
        this.eventFoodSaleRepository.save(sale);

        List<EventFoodSales> sales = this.eventFoodSaleRepository.findByEventTransactionId(transaction.getId());
        BigDecimal total = sales.stream()
                .map(s -> s.getPrice().multiply(BigDecimal.valueOf(s.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        transaction.setValue(total.doubleValue());
        this.eventTransactionRepository.save(transaction);

        return sale;
    }

    public Page<FoodResponseDTO> listAllFoods(Pageable pageable) {
        Page<Foods> foods = this.foodRepository.findAll(pageable);
        return foods.map(foodObject -> new FoodResponseDTO().toResponse(foodObject));
    }

    public Page<FoodResponseDTO> listAllFoodsByEventId(UUID eventId, Pageable pageable) {
        Page<Foods> foods = this.foodRepository.findAllByEvent_Id(eventId, pageable);
        return foods.map(foodObject -> new FoodResponseDTO().toResponse(foodObject));
    }

    public FoodResponseDTO getFoodById(UUID foodId) {

        Foods food = this.foodRepository.findById(foodId).orElseThrow(
                () -> new FoodNotFoundException("food.event.not.found", foodId.toString())
        );

        return new FoodResponseDTO().toResponse(food);
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


}
