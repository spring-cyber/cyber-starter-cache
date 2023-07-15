package com.cyber.infrastructure.toolkit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

//@Component
public class RedisLock {

    public static final Logger LOGGING = LoggerFactory.getLogger(RedisLock.class);

    private static final long DEFAULT_EXPIRE = 30*1000L;
    public static final String UNLOCK_LUA;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    public RedisLock(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");
        UNLOCK_LUA = sb.toString();
    }



    public boolean lock(String lockKey, String lockValue) {
        return this.lock(lockKey, lockValue, DEFAULT_EXPIRE, TimeUnit.MILLISECONDS);
    }


    public boolean lock(String lockKey, String lockValue, long expire, TimeUnit timeUnit) {
        try {
            RedisCallback<Boolean> callback = (connection) -> connection.set(lockKey.getBytes(StandardCharsets.UTF_8),
                    lockValue.getBytes(StandardCharsets.UTF_8), Expiration.seconds(timeUnit.toSeconds(expire)),
                    RedisStringCommands.SetOption.SET_IF_ABSENT);
            return redisTemplate.execute(callback);
        } catch (Exception e) {
            LOGGING.error("redis lock error ,lock key: {}, value : {}, error info : {}", lockKey, lockValue, e);
        }
        return false;
    }


    public boolean unlock(String lockKey, String lockValue) {
        RedisCallback<Boolean> callback = (connection) -> connection.eval(UNLOCK_LUA.getBytes(), ReturnType.BOOLEAN, 1, lockKey.getBytes(StandardCharsets.UTF_8), lockValue.getBytes(StandardCharsets.UTF_8));
        return redisTemplate.execute(callback);
    }


    public String get(String lockKey) {
        try {
            RedisCallback<String> callback = (connection) -> new String(Objects.requireNonNull(connection.get(lockKey.getBytes())), StandardCharsets.UTF_8);
            return redisTemplate.execute(callback);
        } catch (Exception e) {
            LOGGING.error("get redis value occurred an exception,the key is {}, error is {}", lockKey, e);
        }
        return null;
    }
}
