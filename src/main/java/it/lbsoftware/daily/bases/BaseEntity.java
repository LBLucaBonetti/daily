package it.lbsoftware.daily.bases;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Getter
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.PRIVATE)
    protected UUID uuid;
    @Column(updatable = false, nullable = false)
    @CreationTimestamp
    protected LocalDateTime createdAt;
    @Column
    @UpdateTimestamp
    protected LocalDateTime updatedAt;
    @Column
    @Version
    protected int version;

    @PrePersist
    private void generateUuid() {
        setUuid(UUID.randomUUID());
    }

}