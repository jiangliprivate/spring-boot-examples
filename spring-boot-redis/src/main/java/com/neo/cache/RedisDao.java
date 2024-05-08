package com.neo.cache;


import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import com.neo.util.JsonUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 该DAO封装了对redis的访问, 以及从redis存取数据时的序列化和反序列化, 所有方法的名称与redis命令完全保持一致
 * @Auth: William
 * @date: 2019-06-06$ 17:15$
 */
@Component
public class RedisDao {
    protected final StringRedisTemplate redisTemplate;
    protected final ValueOperations<String, String> vops;
    protected final HashOperations<String, String, String> hops;
    protected final SetOperations<String, String> sops;

    public RedisDao(StringRedisTemplate redisTemplate) {
        super();
        this.redisTemplate = redisTemplate;
        this.vops = redisTemplate.opsForValue();
        this.hops = redisTemplate.opsForHash();
        this.sops = redisTemplate.opsForSet();
    }

    /** list结构 begin */
    public <T> List<T> lrange(final String key, final int start, final int stop, Class<T> clazz) {
        List<String> listFromCache = redisTemplate.opsForList().range(key, start, stop);
        return jsonList2ParametricList(listFromCache, clazz);
    }

    public void rpush(final String key, final Object... value) {
        int length = value.length;
        String[] valuesToCache = new String[length];
        for (int i = 0; i < length; i++) {
            valuesToCache[i] = toJsonString(value[i]);
        }
        redisTemplate.opsForList().rightPushAll(key, valuesToCache);
    }

    public void lpush(final String key, final Object... value) {
        int length = value.length;
        String[] valuesToCache = new String[length];
        for (int i = 0; i < length; i++) {
            valuesToCache[i] = toJsonString(value[i]);
        }
        redisTemplate.opsForList().leftPushAll(key, valuesToCache);
    }

    public <T> T rpop(final String key, Class<T> clazz) {
        return parseObject(redisTemplate.opsForList().rightPop(key), clazz);
    }

    public <T> T lpop(final String key, Class<T> clazz) {
        return parseObject(redisTemplate.opsForList().leftPop(key), clazz);
    }

    public long llen(final String key) {
        return redisTemplate.opsForList().size(key);
    }

    /** list结构 end */

    /** set结构 begin */
    public long sadd(final String key, final Object... value) {
        int length = value.length;
        String[] valuesToCache = new String[length];
        for (int i = 0; i < length; i++) {
            valuesToCache[i] = toJsonString(value[i]);
        }
        return redisTemplate.opsForSet().add(key, valuesToCache);
    }

    public <T> T srandmember(final String key, Class<T> clazz) {
        return parseObject(redisTemplate.opsForSet().randomMember(key), clazz);
    }

    public <T> List<T> srandmember(final String key, final long count, Class<T> clazz) {
        List<String> jsonList = redisTemplate.opsForSet().randomMembers(key, count);
        return jsonList2ParametricList(jsonList, clazz);
    }

    /** set结构 end */

    /** hash结构 begin */
    public void hset(final String key, final String field, final Object value) {
        hops.put(key, field, toJsonString(value));
    }

    public boolean hsetnx(final String key, final String field, final Object value) {
        Boolean b = hops.putIfAbsent(key, field, toJsonString(value));
        return null != b ? b.booleanValue() : false;
    }

    public void hmset(final String key, final Map<String, String> fieldValueMap) {
        hops.putAll(key, fieldValueMap);
    }

    public <T> List<T> hmget(final String key, final List<Object> fields, Class<T> clazz) {
        List<Object> objectList = redisTemplate.opsForHash().multiGet(key, fields);
        if (objectList == null) {
            return null;
        }

        List<T> tList = new ArrayList<>(objectList.size());
        for (Object object : objectList) {
            T t = parseObject(object.toString(), clazz);
            tList.add(t);
        }
        return tList;
    }

    public String hget(final String key, final String field) {
        return parseObject(hops.get(key, field), String.class);
    }

    public Map<String, String> hgetall(final String key) {
        return hops.entries(key);
    }

    public <T> T hget(final String key, final String field, Class<T> clazz) {
        return parseObject(hops.get(key, field), clazz);
    }

    public long hdel(final String key, final String field) {
        Long delCnt = hops.delete(key, field);
        return null != delCnt ? delCnt.longValue() : 0;
    }

    public Set<String> hkeys(final String key) {
        return hops.keys(key);
    }

    public <T> Map<String, T> hentries(final String key, Class<T> hvalType) {
        Map<String, String> tmpMap = hops.entries(key);
        if (null == tmpMap) {
            return null;
        } else if (tmpMap.isEmpty()) {
            return new HashMap<String, T>();
        }
        Map<String, T> map = new HashMap<String, T>();
        for (Map.Entry<String, String> entry : tmpMap.entrySet()) {
            if (null != entry.getValue() && entry.getValue().length() > 0) {
                map.put(entry.getKey(), parseObject(entry.getValue(), hvalType));
            }
        }
        return map;
    }

    /** hash结构 end */

    public boolean setnx(final String key, final Object value) {
        Boolean b = redisTemplate.opsForValue().setIfAbsent(key, toJsonString(value));
        return null != b && b;
    }

    public void setex(final String key, final long seconds, final Object value) {
        redisTemplate.opsForValue().set(key, toJsonString(value), seconds, TimeUnit.SECONDS);
    }

    /**
     * 类似于执行set key value ex seconds nx的效果
     * @param key 缓存key
     * @param seconds 缓存失效的时间, 单位秒
     * @param value 缓存value
     * @author William(bysljie@163.com)
     * @return 设置成功还是失败
     */
    public boolean setexnx(final String key, final long seconds, final Object value) {
        String redisResult = redisTemplate.execute((RedisCallback<String>) connection -> {
            Object nativeConnection = connection.getNativeConnection();
            String result = null;
            String valueStr = toJsonString(value);
            // redis3.x集群版本
            if (nativeConnection instanceof JedisCluster) {
                result = ((JedisCluster) nativeConnection).set(key, valueStr, "NX", "EX", seconds);
            }
            // redis2.x单机版本
            if (nativeConnection instanceof Jedis) {
                result = ((Jedis) nativeConnection).set(key, valueStr, "NX", "EX", seconds);
            }
            return result;
        });

        return "OK".equalsIgnoreCase(redisResult);
    }

    /**
     * 给key的值加1
     */
    public Long incr(final String key) {
        return redisTemplate.opsForValue().increment(key, 1L);
    }

    /**
     * 给key的值加指定的value值
     */
    public Long incrby(final String key, final long value) {
        return redisTemplate.opsForValue().increment(key, value);
    }

    public void set(final String key, final Object value) {
        redisTemplate.opsForValue().set(key, toJsonString(value));
    }

    public <T> T get(final String key, Class<T> clazz) {
        return parseObject(redisTemplate.opsForValue().get(key), clazz);
    }

 /*   *//**
     * 缓存的数据结构为string, 且value为List&lt;T&gt;类型, 例如List&lt;User&gt;,
     * List&lt;Bean&gt;等
     * @param key 缓存key
     * @author William(bysljie@163.com)
     *//*
    public <T> List<T> getParametric(final String key, final Class<T> clazz) {
        return JsonUtils.parseParametricFromJson(redisTemplate.opsForValue().get(key), clazz, List.class);
    }*/

    public boolean expire(final String key, final long seconds) {
        Boolean b = redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
        return null != b && b.booleanValue();
    }

    public boolean expire(final String key, final long ttl, final TimeUnit timeUnit) {
        Boolean b = redisTemplate.expire(key, ttl, timeUnit);
        return null != b && b;
    }

    public void del(final String... keys) {
        if(keys!=null && keys.length>0) {
            List<String> keyList = new ArrayList<>(keys.length);
            for(String key: keys) {
                keyList.add(key);
            }
            redisTemplate.delete(keyList);
        }
    }

    public boolean hasKey(final String key) {
        Boolean b = redisTemplate.hasKey(key);
        return null != b && b;
    }

    public <T> T execute(RedisCallback<T> action) {
        return redisTemplate.execute(action, true);
    }

    /**
     * 发送topic
     * @param channelTopicName
     * @param messageObj
     * @author William(bysljie@163.com)
     */
    public void convertAndSendTopic(String channelTopicName, Object messageObj) {
        redisTemplate.convertAndSend(channelTopicName, toJsonString(messageObj));
    }

    @SuppressWarnings("unchecked")
    private static <T> T parseObject(String val, Class<T> clazz) {
        if (val == null) {
            return null;
        }

        if (clazz == String.class) {
            return (T) val;
        }

        return JsonUtils.parseFromJson(val, clazz);
    }

    /**
     * 吧对象序列化成字符串, 如果是字符串类型, 则不序列化, 保持不变
     *
     * @param o 待序列化的对象
     * @return 序列化后的json字符串
     * @author William(bysljie@163.com)
     */
    private String toJsonString(Object o) {
        String value;
        if (o instanceof String) {
            value = (String) o;
        } else {
            value = JsonUtils.toJson(o);
        }
        return value;
    }

    private <T> List<T> jsonList2ParametricList(List<String> jsonList, Class<T> clazz) {
        if (jsonList == null) {
            return null;
        }
        List<T> parametricList = new ArrayList<>(jsonList.size());
        for (Object object : jsonList) {
            T t = parseObject(object.toString(), clazz);
            parametricList.add(t);
        }
        return parametricList;
    }
}
