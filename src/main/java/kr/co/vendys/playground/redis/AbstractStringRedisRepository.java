package kr.co.vendys.playground.redis;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class AbstractStringRedisRepository<ENTITY, ID> implements StringRedisRepository<ENTITY, ID> {

    private final RedisTemplate<String, String> redisTemplate;
    private final Gson gson;

    protected AbstractStringRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.gson = fromGson();

        requiredKeyAnnotation(getEntityClass());
    }

    @Override
    public ENTITY save(ENTITY entity, Long ttl) {
        String key = getKey(findKey(entity));
        String value = gson.toJson(entity);
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value, ttl, TimeUnit.SECONDS);
        return entity;
    }

    @Override
    public ENTITY save(ENTITY entity) {
        String key = getKey(findKey(entity));
        String value = gson.toJson(entity);
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);
        return entity;
    }

    @Override
    public Optional<ENTITY> findById(ID id) {
        String key = getKey(id);
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String value = valueOperations.get(key);
        return Optional.ofNullable(gson.fromJson(value, getEntityClass()));
    }

    @Override
    public void deleteById(ID id) {
        String key = getKey(id);
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.getAndDelete(key);
    }

    @SuppressWarnings("unchecked")
    private ID findKey(ENTITY entity) {
        Class<?> entityClass = entity.getClass();
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Key.class)) {
                try {
                    field.setAccessible(true);
                    return (ID) field.get(entity);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        throw new RuntimeException("@Key annotation not found");
    }

    private String getKey(ID id) {
        for (Field field : getEntityClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Key.class)) {
                Key key = field.getAnnotation(Key.class);
                String prefix = key.prefix();
                return StringUtils.hasText(prefix) ? prefix + id : id.toString();
            }
        }

        return id.toString();
    }

    private static Gson fromGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateSerializer());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
        return gsonBuilder.create();
    }

    private static <T> void requiredKeyAnnotation(Class<T> clz) {
        boolean hasKeyAnnotation = false;
        int keyCount = 0;
        for (Field field : clz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Key.class)) {
                keyCount++;
            }

            if (keyCount > 1) {
                break;
            }
        }

        for (Field field : clz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Key.class)) {
                hasKeyAnnotation = true;
                break;
            }
        }

        Assert.isTrue(hasKeyAnnotation, "@Key annotation not found");
        Assert.isTrue(keyCount == 1, "Only one field can be annotated with @Key");
    }

    public abstract Class<ENTITY> getEntityClass();
}
