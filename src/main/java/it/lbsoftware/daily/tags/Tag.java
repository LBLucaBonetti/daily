package it.lbsoftware.daily.tags;

import com.fasterxml.jackson.annotation.JsonBackReference;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.bases.BaseEntity;
import it.lbsoftware.daily.notes.Note;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.Set;

@Table(indexes = @Index(name = "idx_tag_uuid", columnList = "uuid"))
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appUserId")
    @JsonBackReference
    @NotNull
    private AppUser appUser;

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
