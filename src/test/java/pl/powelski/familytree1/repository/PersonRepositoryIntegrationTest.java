package pl.powelski.familytree1.repository;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import pl.powelski.familytree1.model.Person;
import pl.powelski.familytree1.model.Sex;

@DataJpaTest // Konfiguruje testy dla warstwy JPA, w tym H2 in-memory i Liquibase
@ActiveProfiles("test") // Aktywuje profil "test", wczytując application-test.properties
// Opcjonalnie: @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// Jeśli chcemy być absolutnie pewni, że używana jest konfiguracja z application-test.properties
// Jednak @DataJpaTest z H2 na classpath zwykle dobrze sobie radzi.
// Dla pewności, że Liquibase działa na oddzielnej bazie testowej, można też użyć
// @TestPropertySource(properties = "spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml")
// chociaż @ActiveProfiles("test") powinno to załatwić przez application-test.properties
public class PersonRepositoryIntegrationTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    void shouldSaveAndRetrievePerson() {
        Person person = male()
                .firstName("Jan")
                .lastName("Kowalski")
                .age(30)
                .build();

        Person savedPerson = personRepository.save(person);

        assertThat(savedPerson).isNotNull();
        assertThat(savedPerson.getId()).isNotNull();
        assertThat(savedPerson.getFirstName()).isEqualTo("Jan");

        Optional<Person> foundPersonOpt = personRepository.findById(savedPerson.getId());
        assertThat(foundPersonOpt).isPresent();
        assertThat(foundPersonOpt.get().getLastName()).isEqualTo("Kowalski");
    }

    @Test
    void shouldHandleParentChildRelationship() {
        Person parent = male().firstName("Adam").lastName("Rodzic").age(55).build();
        Person child1 = female().firstName("Ewa").lastName("Dziecko").age(25).build();
        Person child2 = male().firstName("Kain").lastName("Dziecko").age(28).build();


        parent = personRepository.save(parent);
        child1 = personRepository.save(child1);
        child2 = personRepository.save(child2);

        // Ustawianie relacji za pomocą metod pomocniczych
        parent.addChild(child1); // To również ustawi child1.addParent(parent)
        parent.addChild(child2);

        // Save the parent again to persist the relationship changes.
        // Due to CascadeType.PERSIST and MERGE, this should propagate.
        Person savedParent = personRepository.save(parent);

        // Weryfikacja
        Optional<Person> foundParentOpt = personRepository.findByIdWithRelations(savedParent.getId());
        assertThat(foundParentOpt).isPresent();
        Person foundParent = foundParentOpt.get();

        assertThat(foundParent.getChildren()).hasSize(2);
        assertThat(foundParent.getChildren()).extracting(Person::getFirstName).containsExactlyInAnyOrder("Ewa", "Kain");

        Optional<Person> foundChild1Opt = personRepository.findByIdWithRelations(child1.getId());
        assertThat(foundChild1Opt).isPresent();
        Person foundChild1 = foundChild1Opt.get();
        assertThat(foundChild1.getParents()).hasSize(1);
        assertThat(foundChild1.getParents()).extracting(Person::getFirstName).containsExactly("Adam");

        Optional<Person> foundChild2Opt = personRepository.findByIdWithRelations(child2.getId());
        assertThat(foundChild2Opt).isPresent();
        Person foundChild2 = foundChild2Opt.get();
        assertThat(foundChild2.getParents()).hasSize(1);
        assertThat(foundChild2.getParents()).extracting(Person::getFirstName).containsExactly("Adam");
    }

    @Test
    void shouldSaveAndRetrievePersonWithParentsAndChildren() {
        Person grandParent = male().firstName("Dziadek").lastName("Rodzina").age(80).build();
        Person father = male().firstName("Ojciec").lastName("Rodzina").age(50).build();
        Person mother = female().firstName("Matka").lastName("Inna").age(48).build();
        Person child = male().firstName("Dziecko").lastName("Rodzina").age(20).build();

        // **Crucial Change:** Save all individual entities first to ensure they are managed.
        grandParent = personRepository.save(grandParent);
        father = personRepository.save(father);
        mother = personRepository.save(mother);
        child = personRepository.save(child); // Child is now a managed entity

        // Establish relationships using the *managed* entities
        // Dziadek jest rodzicem Ojca
        grandParent.addChild(father);

        // Ojciec i Matka są rodzicami Dziecka
        // Use the managed 'child' object, which was returned from its own save.
        father.addChild(child);
        mother.addChild(child);

        // **Crucial Change:** Save the entities that "own" the relationships to trigger persistence.
        // The 'parents' relationship on 'child' is the owning side because it has @JoinTable.
        // Therefore, saving the child will persist its parent relationships.
        // We also need to save grandParent to persist the grandParent-father relationship.
        personRepository.save(grandParent); // Persists grandParent-father relationship
        personRepository.save(father);      // Ensures father's changes are saved if any
        personRepository.save(mother);      // Ensures mother's changes are saved if any
        personRepository.save(child);       // This will correctly save child's parents (father and mother)


        // Verification - Child
        Optional<Person> foundChildOpt = personRepository.findByIdWithRelations(child.getId());
        assertThat(foundChildOpt).isPresent();
        Person foundChild = foundChildOpt.get();
        assertThat(foundChild.getFirstName()).isEqualTo("Dziecko");
        assertThat(foundChild.getParents()).hasSize(2);
        assertThat(foundChild.getParents()).extracting(Person::getFirstName).containsExactlyInAnyOrder("Ojciec",
                "Matka");

        // Verification - Father
        Optional<Person> foundParent1Opt = personRepository.findByIdWithRelations(father.getId());
        assertThat(foundParent1Opt).isPresent();
        Person foundParent1 = foundParent1Opt.get();
        assertThat(foundParent1.getFirstName()).isEqualTo("Ojciec");
        assertThat(foundParent1.getSex()).isEqualTo(Sex.MALE);
        assertThat(foundParent1.getChildren()).hasSize(1);
        assertThat(foundParent1.getChildren()).extracting(Person::getFirstName).containsExactly("Dziecko");
        assertThat(foundParent1.getParents()).hasSize(1);
        assertThat(foundParent1.getParents()).extracting(Person::getFirstName).containsExactly("Dziadek");

        // Verification - Mother
        Optional<Person> foundParent2Opt = personRepository.findByIdWithRelations(mother.getId());
        assertThat(foundParent2Opt).isPresent();
        Person foundParent2 = foundParent2Opt.get();
        assertThat(foundParent2.getFirstName()).isEqualTo("Matka");
        assertThat(foundParent2.getSex()).isEqualTo(Sex.FEMALE);
        assertThat(foundParent2.getChildren()).hasSize(1);
        assertThat(foundParent2.getChildren()).extracting(Person::getFirstName).containsExactly("Dziecko");
        assertThat(foundParent2.getParents()).isEmpty();

        // Verification - Grandparent
        Optional<Person> foundGrandParentOpt = personRepository.findByIdWithRelations(grandParent.getId());
        assertThat(foundGrandParentOpt).isPresent();
        Person foundGrandParent = foundGrandParentOpt.get();
        assertThat(foundGrandParent.getFirstName()).isEqualTo("Dziadek");
        assertThat(foundGrandParent.getSex()).isEqualTo(Sex.MALE);
        assertThat(foundGrandParent.getChildren()).hasSize(1);
        assertThat(foundGrandParent.getChildren()).extracting(Person::getFirstName).containsExactly("Ojciec");
        assertThat(foundGrandParent.getParents()).isEmpty();
    }

    private static Person.PersonBuilder male() {
        return Person.builder().sex(Sex.MALE);
    }

    private static Person.PersonBuilder female() {
        return Person.builder().sex(Sex.FEMALE);
    }
}
