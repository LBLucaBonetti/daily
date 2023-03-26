package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.bases.BaseEntity;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.notes.Note;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
      @Index(name = "idx_tag_uuid", columnList = "uuid"),
      @Index(name = "idx_tag_appuser", columnList = "app_user")
    })
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Tag extends BaseEntity {

  @Column(nullable = false, length = Constants.TAG_NAME_MAX)
  @NotBlank
  /*
   Display name for this tag
  */
  private String name;

  @Column(nullable = false)
  @NotNull
  @Pattern(regexp = Constants.TAG_COLOR_HEX_REGEXP)
  /*
   Hex encoding string of the display color for this tag
  */
  private String colorHex;

  @ManyToMany(mappedBy = "tags")
  @Builder.Default
  /*
   Notes set with this tag
  */
  private Set<Note> notes = new HashSet<>();

  @Column(name = "app_user", updatable = false, nullable = false)
  @NotNull
  /*
  Unique user id
  */
  private UUID appUser;

  /**
   * Adds a tag to the specified note and vice-versa
   *
   * @param note Note object to link
   */
  public void addToNote(Note note) {
    note.getTags().add(this);
    this.getNotes().add(note);
  }

  /**
   * Removes a tag from the specified note and vice-versa
   *
   * @param note Note object to unlink
   */
  public void removeFromNote(Note note) {
    note.getTags().remove(this);
    this.getNotes().remove(note);
  }
}
