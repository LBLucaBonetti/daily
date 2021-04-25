package it.lbsoftware.daily.notes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.tags.Tag;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(nullable = false)
    @NotBlank
    /**
     * Text for this note
     */
    private String text;
    /**
     * Set when this note is created
     */
    private LocalDateTime creationDateTime;
    /**
     * Set whenever this note is accessed
     */
    private LocalDateTime lastAccessDateTime;
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "noteTag",
            joinColumns = @JoinColumn(name = "noteId"),
            inverseJoinColumns = @JoinColumn(name = "tagId")
    )
    /**
     * Tags set for this note
     */
    private Set<Tag> tagSet = new HashSet<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appUserId")
    @JsonBackReference
    private AppUser appUser;

}
