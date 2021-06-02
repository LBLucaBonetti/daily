package it.lbsoftware.daily.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;
    @Column(updatable = false, nullable = false)
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
