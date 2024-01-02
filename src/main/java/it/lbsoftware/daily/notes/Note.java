package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.bases.BaseEntity;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.tags.Tag;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(
    indexes = {
      @Index(name = "idx_note_uuid", columnList = "uuid"),
      @Index(name = "idx_note_appuser", columnList = "app_user")
    })
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Note extends BaseEntity {

  @Column(nullable = false, length = Constants.NOTE_TEXT_MAX)
  @NotBlank
  /*
   Text for this note
  */
  private String text;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "noteTag",
      joinColumns = @JoinColumn(name = "noteId"),
      inverseJoinColumns = @JoinColumn(name = "tagId"))
  @Builder.Default
  /*
   Tags set for this note
  */
  private Set<Tag> tags = new HashSet<>();

  @Column(name = "app_user", updatable = false, nullable = false)
  @NotNull
  /*
  Unique user id
  */
  private UUID appUser;

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }
}
