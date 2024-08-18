package it.lbsoftware.daily.money;

import org.springframework.data.jpa.repository.JpaRepository;

/** Main {@link Money} repository. */
public interface MoneyRepository extends JpaRepository<Money, Long> {}
