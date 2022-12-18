package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.bases.BaseEntity;
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

  @Column(nullable = false)
  @NotBlank
  /*
   Display name for this tag
  */
  private String name;

  @Column(nullable = false)
  @NotNull
  @Pattern(regexp = "^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$")
  /*
   Hex encoding string of the display color for this tag
  */
  private String colorHex;

  @ManyToMany(mappedBy = "tagSet")
  @Builder.Default
  /*
   Notes set with this tag
  */
  private Set<Note> noteSet = new HashSet<>();

  @Column(name = "app_user", nullable = false)
  @NotBlank
  /*
  Unique user id
  */
  private String appUser;

  /**
   * Adds a tag to the specified note and vice-versa
   *
   * @param note Note object to link
   */
  public void addToNote(Note note) {
    note.getTagSet().add(this);
    this.getNoteSet().add(note);
  }

  /**
   * Removes a tag from the specified note and vice-versa
   *
   * @param note Note object to unlink
   */
  public void removeFromNote(Note note) {
    note.getTagSet().remove(this);
    this.getNoteSet().remove(note);
  }
}
