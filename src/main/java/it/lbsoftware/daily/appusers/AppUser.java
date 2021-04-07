package it.lbsoftware.daily.appusers;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
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
