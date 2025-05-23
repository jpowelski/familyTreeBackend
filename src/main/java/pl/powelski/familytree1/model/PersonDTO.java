package pl.powelski.familytree1.model;

import java.util.HashSet;
import java.util.Set;

public class PersonDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private Integer age;

    private Sex sex;

    private Set<PersonDTO> parents = new HashSet<>();

    private Set<PersonDTO> children = new HashSet<>();
}
