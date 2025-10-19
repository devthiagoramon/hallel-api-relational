package br.hallel.relational.api.app.association.repository;

import br.hallel.relational.api.app.association.model.Associate;
import br.hallel.relational.api.app.association.model.AssociationPayment;
import br.hallel.relational.api.app.association.model.AssociatePaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssociatePaymentRepository extends JpaRepository<AssociationPayment, UUID> {
    Optional<AssociationPayment> findTopByAssociateAndStatusOrderByPaidDateDesc(Associate associate, AssociatePaymentStatus status);

    Optional<AssociationPayment> findByMercadoPagoPaymentId(Long mercadoPagoPaymentId);

    List<AssociationPayment> findByAssociate(Associate associate);
}
