package br.hallel.relational.api.app.user.repository;

import br.hallel.relational.api.app.user.model.LastAccessLog;
import br.hallel.relational.api.app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface LastAcessLogRepository extends JpaRepository<LastAccessLog, UUID> {
    List<LastAccessLog> findTopByUserOrderByAccessedAt(User user);

    @Query("SELECT MAX(l.accessedAt) FROM LastAccessLog l WHERE l.user = :user")
    LocalDateTime findLastAccessDateByUser(@Param("user") User user);
}
