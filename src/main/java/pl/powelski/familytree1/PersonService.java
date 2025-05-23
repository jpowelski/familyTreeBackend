package pl.powelski.familytree1;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pl.powelski.familytree1.model.Person;
import pl.powelski.familytree1.model.PersonMapper;
import pl.powelski.familytree1.model.PersonWithRelativesDTO;
import pl.powelski.familytree1.repository.PersonRepository;

@RequiredArgsConstructor
@Service
class PersonService {

    private final PersonRepository personRepository;

    private final PersonMapper personMapper;

    List<PersonWithRelativesDTO> findAll() {
        final List<Person> personList = personRepository.findAll();
        return personList.stream().map(personMapper::personToPersonWithRelativesDTO).toList();
    }

}
