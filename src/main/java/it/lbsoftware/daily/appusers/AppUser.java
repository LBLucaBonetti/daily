package it.lbsoftware.daily.appusers;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import it.lbsoftware.daily.bases.BaseEntity;
import it.lbsoftware.daily.notes.Note;
import it.lbsoftware.daily.tags.Tag;
import lombok.*;

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
@Setter
@Builder
public class AppUser extends BaseEntity {

    @Column(unique = true, nullable = false)
    @NotBlank
    /*
      Okta unique identifier for this user
     */
    private String uid;
    @Column(unique = true, nullable = false)
    @Email
    @NotNull
    /*
      Okta registration email for this user
     */
    private String email;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "appUser")
    @JsonManagedReference
    /*
      Private tags for this user
     */
    private List<Tag> tagList = new ArrayList<>();
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "appUser")
    @JsonManagedReference
    /*
      Private notes for this user
     */
    private List<Note> noteList = new ArrayList<>();

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }

        if (object instanceof AppUser) {
            AppUser other = (AppUser) object;
            return this.getUid().equals(other.getUid());
        }

        return false;
    }

}