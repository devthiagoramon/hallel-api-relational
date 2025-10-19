package br.hallel.relational.api.app.user.model;

import br.hallel.relational.api.app.messaging.mobile.model.DeviceNotification;
import br.hallel.relational.api.app.security.model.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nimbusds.jose.util.Pair;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@Entity(name = "User")
@Table(name = "\"user\"")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties({"password", "roles", "token", "enabled", "accountNonLocked", "accountNonExpired", "credentialsNonExpired"})
public class User implements Serializable, UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)

    private String password;
    @Column(nullable = false)
    private String token;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_birth")
    private Date dateBirth;

    @Column(name = "phone_number", length = 11)
    private String phoneNumber;

    @Column(name = "file_image_url", length = 999)
    private String fileImageUrl;

    @Column(length = 11)
    private String cpf;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    private UserAccountStatus status;

    private Integer age;

    @JsonIgnore
    @Column(name = "push_notification")
    private Boolean pushNotification;

    @JsonIgnore
    @Column(name = "lgpd_consent")
    private Boolean lgpdConsent;

    @JsonIgnore
    @Column(name = "lgpd_consent_date")
    private Date lgpdConsentDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_device_notification",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "device_notification_id")
    )
    @JsonIgnore
    private List<DeviceNotification> devicesUser;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<LastAccessLog> lastAccessLogs;

    @JsonIgnore
    @Column(name = "birth_day_email_sent_in")
    private LocalDate birthdayEmailSentIn;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getDescription())).toList();
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return this.password;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return this.email;
    }

}
