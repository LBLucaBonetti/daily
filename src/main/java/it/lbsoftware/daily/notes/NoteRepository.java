package it.lbsoftware.daily.notes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    /**
     * Finds notes containing a certain string
     *
     * @param contained The string to be found
     * @return Found notes or empty list
     */
    List<Note> findByTextContaining(String contained);

}
