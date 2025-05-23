package pl.powelski.familytree1.model;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonWithRelativesDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private Integer age;
    private Sex sex;
    private Set<PersonShallowDTO> parents;
    private Set<PersonShallowDTO> children;
}
