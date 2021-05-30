package it.lbsoftware.daily.tags;

import com.fasterxml.jackson.annotation.JsonBackReference;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.entities.BaseEntity;
import it.lbsoftware.daily.notes.Note;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

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
    @NotBlank
    /*
      Hex encoding string of the display color for this tag
     */
    private String colorHex;
    @ManyToMany(mappedBy = "tagSet")
    /*
      Notes set with this tag
     */
    private Set<Note> noteSet = new HashSet<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appUserId")
    @JsonBackReference
    private AppUser appUser;

}
