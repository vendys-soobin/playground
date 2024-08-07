package kr.co.vendys.playground.domain.person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

@SpringBootTest
class PersonServiceTest {

    @Autowired
    private PersonService personService;

    @Autowired
    private PersonRepository personRepository;

    @Test
    void save() {
        // given
        Person person = new Person("홍길동", "01012345678");

        // when
        personService.save(person);

        // then
        Person savedPerson = personService.findById(person.getPersonId());
        assertThat(person.getPersonId()).isEqualTo(savedPerson.getPersonId());
        assertThat(person.getName()).isEqualTo(savedPerson.getName());
        assertThat(person.getPhoneNumber()).isEqualTo(savedPerson.getPhoneNumber());
    }

    @Test
    void findById() {
        // given
        Person person = new Person("홍길동", "01012345678");
        personRepository.save(person);

        // when
        Person savedPerson = personService.findById(person.getPersonId());

        // then
        assertThat(person.getPersonId()).isEqualTo(savedPerson.getPersonId());
        assertThat(person.getName()).isEqualTo(savedPerson.getName());
        assertThat(person.getPhoneNumber()).isEqualTo(savedPerson.getPhoneNumber());
    }
}
