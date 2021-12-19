package it.lbsoftware.daily.appusers;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import it.lbsoftware.daily.bases.BaseEntity;
import it.lbsoftware.daily.notes.Note;
import it.lbsoftware.daily.tags.Tag;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Table(indexes = @Index(name = "idx_appuser_uuid", columnList = "uuid"))
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class AppUser extends BaseEntity {

    @Column(updatable = false, nullable = false, unique = true)
    @NotBlank
    /*
      Okta unique identifier for this user
     */
    private String uid;
    @Column(nullable = false, unique = true)
    @Email
    @NotNull
    @Setter
    /*
      Okta registration email for this user
     */
    private String email;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "appUser")
    @JsonManagedReference
    @Setter
    @Builder.Default
    /*
      Private tags for this user
     */
    private List<Tag> tagList = new ArrayList<>();
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "appUser")
    @JsonManagedReference
    @Setter
    @Builder.Default
    /*
      Private notes for this user
     */
    private List<Note> noteList = new ArrayList<>();

    /**
     * Adds a note to this app user and vice-versa
     *
     * @param note Note object to link
     */
    public void addNote(Note note) {
        note.setAppUser(this);
        this.noteList.add(note);
    }

    /**
     * Removes a note from this app user and vice-versa
     *
     * @param note Note object to unlink
     */
    public void removeNote(Note note) {
        note.setAppUser(null);
        this.noteList.remove(note);
    }

    /**
     * Adds a tag to this app user and vice-versa
     *
     * @param tag Tag object to link
     */
    public void addTag(Tag tag) {
        tag.setAppUser(this);
        this.tagList.add(tag);
    }

    /**
     * Removes a tag from this app user and vice-versa
     *
     * @param tag Tag object to unlink
     */
    public void removeTag(Tag tag) {
        tag.setAppUser(null);
        this.tagList.remove(tag);
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(uid);
        return hcb.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof AppUser))
            return false;
        AppUser that = (AppUser) obj;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(uid, that.uid);
        return eb.isEquals();
    }

}