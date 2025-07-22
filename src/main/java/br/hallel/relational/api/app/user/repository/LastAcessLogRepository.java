package br.hallel.relational.api.app.user.repository;

import br.hallel.relational.api.app.user.model.LastAcessLog;
import br.hallel.relational.api.app.user.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LastAcessLogRepository extends CrudRepository<LastAcessLog, Long> {
    List<LastAcessLog> findTopByUserOrderByAccessedAt(User user);

    @Query("SELECT MAX(l.accessedAt) FROM LastAcessLog l WHERE l.user = :user")
    LocalDateTime findLastAccessDateByUser(@Param("user") User user);
}
