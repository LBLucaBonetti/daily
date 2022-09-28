package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.bases.BaseEntity;
import it.lbsoftware.daily.notes.Note;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(
    indexes = {
      @Index(name = "idx_tag_uuid", columnList = "uuid"),
      @Index(name = "idx_tag_appuser", columnList = "appUser")
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
