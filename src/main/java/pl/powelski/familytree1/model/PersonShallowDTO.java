package pl.powelski.familytree1.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonShallowDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private Integer age;
    private Sex sex;
}
