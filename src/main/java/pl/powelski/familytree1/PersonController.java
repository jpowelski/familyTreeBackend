package pl.powelski.familytree1;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pl.powelski.familytree1.model.Person;
import pl.powelski.familytree1.model.PersonWithRelativesDTO;
import pl.powelski.familytree1.repository.PersonRepository;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class PersonController {

    private final PersonRepository personRepository;

    private final PersonService personService;

    @GetMapping("/users")
    public List<PersonWithRelativesDTO> getUsers() {
        return personService.findAll();
    }

    @PostMapping("/users")
    void addUser(@RequestBody Person person) {
        personRepository.save(person);
    }
}
