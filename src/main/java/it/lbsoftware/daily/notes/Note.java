package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.bases.BaseEntity;
import it.lbsoftware.daily.tags.Tag;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
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

  @Column(nullable = false)
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
  private Set<Tag> tagSet = new HashSet<>();

  @Column(name = "app_user", nullable = false)
  @NotBlank
  /*
  Unique user id
  */
  private String appUser;
}
