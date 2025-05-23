package pl.powelski.familytree1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pl.powelski.familytree1.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    // Przykładowa metoda do pobierania osoby wraz z jej rodzicami i dziećmi
    // Użyj FetchType.LAZY w encji i dociągaj dane w razie potrzeby
    // lub zdefiniuj konkretne zapytania z JOIN FETCH
    @Query("SELECT p FROM Person p LEFT JOIN FETCH p.parents LEFT JOIN FETCH p.children WHERE p.id = :id")
    Optional<Person> findByIdWithRelations(Long id);
}
