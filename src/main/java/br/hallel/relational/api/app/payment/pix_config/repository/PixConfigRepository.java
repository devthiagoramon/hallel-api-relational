package br.hallel.relational.api.app.payment.pix_config.repository;

import br.hallel.relational.api.app.payment.pix_config.model.PixConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PixConfigRepository extends JpaRepository<PixConfig, UUID> {

    Optional<PixConfig> findByAtivoTrue();

    @Modifying
    @Query("UPDATE PixConfig p SET p.ativo = false WHERE p.ativo = true")
    void deactivateAll();
}
