package id.co.hilmi.bemobile.service.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisManagerService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void putCache(String cacheName, Object key, Object val, long ttl){
        redisTemplate.opsForValue().set(cacheName + key, val, ttl, TimeUnit.SECONDS);
    }

    public Object getCache(String cacheName, Object key) {
        return redisTemplate.opsForValue().get(cacheName + key);
    }

    public void removeCache(String cacheName, Object key) {
        redisTemplate.opsForValue().getOperations().delete(cacheName + key);
    }

    public Object removeCachePersist(String cacheName, String key) {
        var result = this.getCache(cacheName, key);
        if (null != result) {
            removeCache(cacheName, key);
        }
        return result;
    }
}
