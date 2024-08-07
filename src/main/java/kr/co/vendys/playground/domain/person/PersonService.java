package kr.co.vendys.playground.domain.person;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PersonService {

    private final PersonRepository personRepository;

    public void save(Person person) {
        personRepository.save(person);
    }

    public Person findById(String personId) {
        return personRepository.findById(personId)
                .orElseThrow(() -> new IllegalArgumentException("Person not found. personId=" + personId));
    }
}
