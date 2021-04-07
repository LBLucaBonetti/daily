package it.lbsoftware.daily.appusers;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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
    private String uid;
    @Column(unique = true, nullable = false)
    @Email
    @NotNull
    private String email;
    private LocalDateTime registrationDateTime;
    private LocalDateTime lastAccessDateTime;

}
