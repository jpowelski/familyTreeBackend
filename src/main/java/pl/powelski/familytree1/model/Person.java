package pl.powelski.familytree1.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "person")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Zapobiega rekurencyjnym problemom z hashCode/equals
@ToString(exclude = {"parents", "children"}) // Zapobiega rekurencyjnym problemom z toString
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "age")
    private Integer age;

    @Column(name = "sex")
    @Enumerated(EnumType.STRING) //
    private Sex sex;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "person_parent",
            joinColumns = @JoinColumn(name = "child_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_id")
    )
    @Builder.Default
    private Set<Person> parents = new HashSet<>();

    @ManyToMany(mappedBy = "parents", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    private Set<Person> children = new HashSet<>();

    // Metody pomocnicze do zarządzania relacjami (opcjonalne, ale dobre praktyki)
    public void addParent(Person parent) {
        this.parents.add(parent);
        parent.getChildren().add(this);
    }

    public void removeParent(Person parent) {
        this.parents.remove(parent);
        parent.getChildren().remove(this);
    }

    public void addChild(Person child) {
        this.children.add(child);
        child.getParents().add(this);
    }

    public void removeChild(Person child) {
        this.children.remove(child);
        child.getParents().remove(this);
    }
}
