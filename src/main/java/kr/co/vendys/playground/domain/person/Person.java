package kr.co.vendys.playground.domain.person;

import java.util.UUID;

import kr.co.vendys.playground.redis.Key;
import lombok.Getter;

@Getter
public class Person {

    @Key(prefix = "person")
    private final String personId;
    private final String name;
    private final String phoneNumber;

    public Person(String name, String phoneNumber) {
        this.personId = generateId();
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    private static String generateId() {
        return UUID.randomUUID().toString();
    }
}
