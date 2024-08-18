package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.bases.BaseEntity;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.money.Money;
import it.lbsoftware.daily.notes.Note;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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

/** The tag entity. */
@Table(
    indexes = {
      @Index(name = "idx_tag_uuid", columnList = "uuid"),
    })
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Tag extends BaseEntity {

  /** Display name for this tag. */
  @Column(nullable = false, length = Constants.TAG_NAME_MAX)
  @NotBlank
  private String name;

  /** Hex encoding string of the display color for this tag. */
  @Column(nullable = false)
  @NotNull
  @Pattern(regexp = Constants.TAG_COLOR_HEX_REGEXP)
  private String colorHex;

  /** Notes set with this tag. */
  @ManyToMany(mappedBy = "tags")
  @Builder.Default
  private Set<Note> notes = new HashSet<>();

  /** Money set with this tag. */
  @ManyToMany(mappedBy = "tags")
  @Builder.Default
  private Set<Money> money = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "app_user_id",
      updatable = false,
      nullable = false,
      referencedColumnName = "id")
  @NotNull
  private AppUser appUser;

  /**
   * Adds this tag to the specified note and vise versa.
   *
   * @param note Note object to link
   */
  public void addToNote(Note note) {
    note.getTags().add(this);
    this.getNotes().add(note);
  }

  /**
   * Removes this tag from the specified note and vise versa.
   *
   * @param note Note object to unlink
   */
  public void removeFromNote(Note note) {
    note.getTags().remove(this);
    this.getNotes().remove(note);
  }

  /**
   * Adds this tag to the specified money and vise versa.
   *
   * @param money Money object to link
   */
  public void addToMoney(Money money) {
    money.getTags().add(this);
    this.getMoney().add(money);
  }

  /**
   * Removes this tag from the specified money and vise versa.
   *
   * @param money Money object to unlink
   */
  public void removeFromMoney(Money money) {
    money.getTags().remove(this);
    this.getMoney().remove(money);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }
}
