package kr.co.vendys.playground.domain.person;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import kr.co.vendys.playground.redis.AbstractStringRedisRepository;

@Repository
public class PersonRepository extends AbstractStringRedisRepository<Person, String> {

    protected PersonRepository(RedisTemplate<String, String> redisTemplate) {
        super(redisTemplate);
    }

    @Override public Class<Person> getEntityClass() {
        return Person.class;
    }
}
