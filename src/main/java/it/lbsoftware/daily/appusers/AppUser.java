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

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(uid);
        return hcb.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AppUser)) {
            return false;
        }
        AppUser that = (AppUser) obj;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(uid, that.uid);
        return eb.isEquals();
    }

}