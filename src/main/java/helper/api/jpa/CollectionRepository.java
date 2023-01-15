package helper.api.jpa;

import helper.model.Collection;
import helper.model.Language;
import helper.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    @Query("SELECT c FROM Collection c WHERE c.owner = ?1 AND ((c.lang1 = ?2 AND c.lang2 = ?3) OR (c.lang1 = ?3 AND c.lang2 = ?2))")
    List<Collection> findCollectionsByOwnerAndLang1AndLang2(Student student, Language lang1, Language lang2);

    List<Collection> findCollectionsByOwner(Student student);

    Optional<Collection> findCollectionByNameAndOwner(String name, Student owner);
}
