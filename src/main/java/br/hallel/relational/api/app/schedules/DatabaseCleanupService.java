package br.hallel.relational.api.app.schedules;

import br.hallel.relational.api.app.event.model.FoodTransaction;
import br.hallel.relational.api.app.event.model.enum_type.StatusPaymentFood;
import br.hallel.relational.api.app.event.repository.FoodTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;
import java.util.List;
// ... outros imports

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseCleanupService {

    private final FoodTransactionRepository foodTransactionRepository;

    @Scheduled(cron = "0 0 3 * * *", zone = "America/Manaus")
    @Transactional
    public void cleanupAbandonedFoodTransactions() {
        log.info("Iniciando rotina de limpeza de transações de alimentos abandonadas...");

        OffsetDateTime cutoff = OffsetDateTime.now().minusHours(24);

        List<FoodTransaction> abandonedTransactions =
                foodTransactionRepository.findAllByStatusAndDateTransactionBefore(StatusPaymentFood.PENDENTE, cutoff);

        if (!abandonedTransactions.isEmpty()) {
            log.info("Encontradas {} transações abandonadas para deletar.", abandonedTransactions.size());
            foodTransactionRepository.deleteAll(abandonedTransactions);
            log.info("Limpeza concluída com sucesso.");
        } else {
            log.info("Nenhuma transação abandonada encontrada.");
        }
    }
}