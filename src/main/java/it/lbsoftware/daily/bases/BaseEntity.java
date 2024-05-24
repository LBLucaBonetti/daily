package it.lbsoftware.daily.bases;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/** Base entity containing sensible common fields. */
@MappedSuperclass
@Getter
public abstract class BaseEntity {

  private static final String HIBERNATE_SEQUENCE =
      "hibernate_sequence"; // Matches the sequence name specified in the initial migration

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = HIBERNATE_SEQUENCE)
  @SequenceGenerator(
      name = HIBERNATE_SEQUENCE,
      initialValue = 1,
      sequenceName = HIBERNATE_SEQUENCE,
      allocationSize = 50)
  protected Long id;

  @Column(updatable = false, nullable = false, unique = true)
  @Setter(AccessLevel.PRIVATE)
  protected UUID uuid;

  @Column(updatable = false, nullable = false)
  @CreationTimestamp
  protected LocalDateTime createdAt;

  @Column @UpdateTimestamp protected LocalDateTime updatedAt;
  @Column @Version protected int version;

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
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof BaseEntity that)) {
      return false;
    }
    return getId() != null && Objects.equals(getId(), that.getId());
  }
}
