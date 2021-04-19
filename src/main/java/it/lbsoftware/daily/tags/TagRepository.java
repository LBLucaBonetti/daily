package it.lbsoftware.daily.tags;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * Finds tags by their name
     *
     * @param name The name of the tag
     * @return Found tags or empty list
     */
    List<Tag> findByName(String name);

}
