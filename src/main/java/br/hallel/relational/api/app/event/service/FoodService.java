package br.hallel.relational.api.app.event.service;

import br.hallel.relational.api.app.event.dto.EventTransactionResponse;
import br.hallel.relational.api.app.event.dto.FoodEditDTO;
import br.hallel.relational.api.app.event.dto.FoodRequestDTO;
import br.hallel.relational.api.app.event.dto.FoodResponseDTO;
import br.hallel.relational.api.app.event.exception.EventIllegalArumentException;
import br.hallel.relational.api.app.event.exception.EventNotFoundException;
import br.hallel.relational.api.app.event.exception.FoodEmptyListException;
import br.hallel.relational.api.app.event.exception.FoodNotFoundException;
import br.hallel.relational.api.app.event.model.Event;
import br.hallel.relational.api.app.event.model.EventTransaction;
import br.hallel.relational.api.app.event.model.Foods;
import br.hallel.relational.api.app.event.repository.EventRepository;
import br.hallel.relational.api.app.event.repository.EventTransactionRepository;
import br.hallel.relational.api.app.event.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;
    private final EventRepository eventRepository;
    private final EventTransactionRepository eventTransactionRepository;

    public FoodResponseDTO createFood(FoodRequestDTO dto) {

        Foods food = new Foods();
        food.setName(dto.name());
        food.setValue(dto.value());
        food.setStockQuantity(dto.stockQuantity());

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
            throw new EventIllegalArumentException("A quantidade de Alimentos para venda é maior que a quantidade em " +
                    "estoque!");
        }

        EventTransaction transaction = new EventTransaction();

        BigDecimal totalValue = food.getValue().multiply(new BigDecimal(food.getStockQuantity()));

        transaction.setValue(Double.parseDouble(totalValue.toString()));
        transaction.setDesciption("Venda: %s (x%d)".formatted(food.getName(), quantity));
        transaction.setIsEditable(Boolean.FALSE);
        transaction.setEvent(food.getEvent());

        this.eventTransactionRepository.save(transaction);

        food.setStockQuantity(food.getStockQuantity() - quantity);
        this.foodRepository.save(food);

        return EventTransactionResponse.toResponse(transaction);
    }

    public Page<FoodResponseDTO> listAllFoods(Pageable pageable) {
        Page<Foods> foods = this.foodRepository.findAll(pageable);
        return foods.map(foodObject -> new FoodResponseDTO().toResponse(foodObject));
    }

    public Page<FoodResponseDTO> listAllFoodsByEventId(UUID eventId,Pageable pageable) {
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
