package ru.otus.projectwork.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.otus.projectwork.domain.model.enums.AuthorizationType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Профиль клиента
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "client_id", nullable = false, unique = true)
    @ToString.Exclude
    private Client client;

    @Column(nullable = false, length = 65)
    private String password;

    @ColumnDefault("false")
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isSmsEnabled;

    @ColumnDefault("false")
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isPushEnabled;

    @Column(length = 50, unique = true,nullable = false)
    private String email;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    @CreationTimestamp
    private LocalDateTime createDate;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    @UpdateTimestamp
    private LocalDateTime updateDate;

    @ColumnDefault("false")
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isEmailSubscription;

    @Column(length = 4)
    private String authorizationPin;

    @ColumnDefault("LOGIN")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthorizationType authorizationType;

    @Column(columnDefinition="bytea")
    private byte[] profilePicture;

}
