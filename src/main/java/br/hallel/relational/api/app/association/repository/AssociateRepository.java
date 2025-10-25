package br.hallel.relational.api.app.association.repository;

import br.hallel.relational.api.app.association.dto.AssociateWithUserResponse;
import br.hallel.relational.api.app.association.model.Associate;
import br.hallel.relational.api.app.association.model.AssociatePaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<Associate> findByStatusAndRenewalDateBetween(AssociatePaymentStatus status, LocalDateTime renewalDateAfter,
                                                      LocalDateTime renewalDateBefore);

    Optional<Associate> findByUser_Id(UUID userId);

    @Query("""
            SELECT new br.hallel.relational.api.app.association.dto.AssociateWithUserResponse(
                a.id,
                a.user,
                a.status,
                a.associateSince,
                a.renewalDate
                ) FROM Associate a
            """)
    Page<AssociateWithUserResponse> listAllWithUserResponse(Pageable pageable);

    @Query("""
            SELECT new br.hallel.relational.api.app.association.dto.AssociateWithUserResponse(
                a.id,
                a.user,
                a.status,
                a.associateSince,
                a.renewalDate
                ) FROM Associate a
            WHERE a.status = :status
            """)
    Page<AssociateWithUserResponse> listAllWithUserResponseAndPaymentStatus(@Param("status") AssociatePaymentStatus status, Pageable pageable);
}
