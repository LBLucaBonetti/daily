package it.lbsoftware.daily.notes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.bases.BaseEntity;
import it.lbsoftware.daily.tags.Tag;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Table(indexes = @Index(name = "idx_note_uuid", columnList = "uuid"))
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
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "noteTag",
            joinColumns = @JoinColumn(name = "noteId"),
            inverseJoinColumns = @JoinColumn(name = "tagId")
    )
    /*
      Tags set for this note
     */
    private Set<Tag> tagSet = new HashSet<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appUserId")
    @JsonBackReference
    @NotNull
    private AppUser appUser;

}