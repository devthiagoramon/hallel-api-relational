package br.hallel.relational.api.app.event.repository;

import br.hallel.relational.api.app.event.model.FoodTransaction;
import br.hallel.relational.api.app.event.model.enum_type.StatusPaymentFood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FoodTransactionRepository extends JpaRepository<FoodTransaction, UUID> {
    Optional<FoodTransaction> findByMercadoPagoPaymentId(Long mercadoPagoPaymentId);

    List<FoodTransaction> findAllByStatusAndDateTransactionBefore(StatusPaymentFood status, OffsetDateTime dateTransactionBefore);
}
