package br.hallel.relational.api.app.association.repository;

import br.hallel.relational.api.app.association.model.Associate;
import br.hallel.relational.api.app.association.model.AssociatePaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssociateRepository
        extends JpaRepository<Associate, UUID> {
    boolean existsAssociateByUser_Id(UUID userId);

    List<Associate> findByStatusAndRenewalDateBefore(AssociatePaymentStatus status, LocalDateTime renewalDateBefore);

    List<Associate> findByStatusAndRenewalDateBetween(AssociatePaymentStatus status, LocalDateTime renewalDateAfter, LocalDateTime renewalDateBefore);

    Optional<Associate> findByUser_Id(UUID userId);
}
