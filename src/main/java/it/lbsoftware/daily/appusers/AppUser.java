package it.lbsoftware.daily.appusers;

import it.lbsoftware.daily.notes.Note;
import it.lbsoftware.daily.tags.Tag;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(unique = true, nullable = false)
    @NotBlank
    /**
     * Okta unique identifier for this user
     */
    private String uid;
    @Column(unique = true, nullable = false)
    @Email
    @NotNull
    /**
     * Okta registration email for this user
     */
    private String email;
    /**
     * Set when this user is created
     */
    private LocalDateTime registrationDateTime;
    /**
     * Set whenever this user accesses services
     */
    private LocalDateTime lastAccessDateTime;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "appUser")
    /**
     * Private tags for this user
     */
    private List<Tag> tagList;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "appUser")
    /**
     * Private notes for this user
     */
    private List<Note> noteList;

}
