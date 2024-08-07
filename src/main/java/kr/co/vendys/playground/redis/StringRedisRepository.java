package kr.co.vendys.playground.redis;

import java.util.Optional;

public interface StringRedisRepository<ENTITY, ID> {

    ENTITY save(ENTITY entity, Long ttl);

    ENTITY save(ENTITY entity);

    Optional<ENTITY> findById(ID id);

    void deleteById(ID id);
}
