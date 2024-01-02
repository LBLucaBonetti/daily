package it.lbsoftware.daily.appuseractivations;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserActivationRepository extends JpaRepository<AppUserActivation, Long> {

  Optional<AppUserActivation> findByActivationCode(UUID activationCode);
}
