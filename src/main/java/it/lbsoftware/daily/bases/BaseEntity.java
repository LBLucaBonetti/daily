package it.lbsoftware.daily.bases;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@Getter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;
    @Column(updatable = false, nullable = false, unique = true)
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

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof BaseEntity))
            return false;
        BaseEntity that = (BaseEntity) obj;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

}