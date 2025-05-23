package pl.powelski.familytree1.model;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
// componentModel = "spring" sprawia, że MapStruct wygeneruje implementację jako komponent Springa,
// dzięki czemu możesz go wstrzykiwać (@Autowired).
// unmappedTargetPolicy = ReportingPolicy.IGNORE zapobiega ostrzeżeniom/błędom, jeśli nie wszystkie pola są mapowane.
public interface PersonMapper {


    // Metoda do mapowania Person na PersonShallowDTO.
    // MapStruct automatycznie użyje tej metody podczas mapowania kolekcji Set<Person> na Set<PersonShallowDTO>.
    PersonShallowDTO personToPersonShallowDTO(Person person);

    // Główna metoda mapująca Person na PersonWithRelativesDTO.
    // MapStruct automatycznie zastosuje personToPersonShallowDTO dla pól 'parents' i 'children'.
    PersonWithRelativesDTO personToPersonWithRelativesDTO(Person person);

    // Jeśli potrzebujesz również mapowania w drugą stronę (DTO do Encji),
    // musisz zdefiniować odpowiednie metody. Pamiętaj o obsłudze rekurencji
    // lub o tym, jak chcesz odtwarzać relacje z DTO.
    // Np. Person personShallowDTOToPerson(PersonShallowDTO dto);
    //      Person personWithRelativesDTOToPerson(PersonWithRelativesDTO dto); // Tutaj ostrożnie
}
