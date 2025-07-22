package br.hallel.relational.api.app.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "last_access_log")
@NoArgsConstructor
@Getter
public class LastAcessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime accessedAt;

    public LastAcessLog(User user, LocalDateTime accessedAt) {
        this.user = user;
        this.accessedAt = accessedAt;
    }
}
