package com.lzz.common.config;

import com.lzz.common.util.FastJsonRedisSerializer;
import com.lzz.common.util.JedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;


/**
 * Created by aichaellee on 2018/9/29.
 * 如果你想把数据保存在session,那么创建一个类,让Spring容器管理,作用域设置为Sesson,设置session超时时间,超时就会销毁.
 @Scope(scopeName = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
 如果要完全自己写,那么指定session在REDIS 里面的key,然后保存用户session的时候,也自定义SESSION id,
 控制SESSION,名单,然后可以用JEDIS 或者封装后的Spring data redis操作

 * 后期我要使用Oauth + JWT进行重构

 */
@Configuration
@EnableCaching
public class RedisCacheConfig extends CachingConfigurerSupport{
    Logger logger = LoggerFactory.getLogger(RedisCacheConfig.class);

    @Value("${spring.redis.url}")
    private String url;

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.pool.max-wait}")
    private long maxWaitMillis;

    @Value("${spring.redis.password}")
    private String password;


//    @Bean
//    public JedisPool redisPoolFactory() {
//        logger.info("JedisPool注入成功！！");
//        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//        jedisPoolConfig.setMaxIdle(maxIdle);
//        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
//        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host,port,timeout,password);
//
//        return jedisPool;
//    }
//
//
//    @Bean
//    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory cf) {
//        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(cf);
//        return redisTemplate;
//    }


    @Bean(name = "redisTemplate")
    @SuppressWarnings("unchecked")
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();

        //使用fastjson序列化
        FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);
        // value值的序列化采用fastJsonRedisSerializer
        template.setValueSerializer(fastJsonRedisSerializer);
        template.setHashValueSerializer(fastJsonRedisSerializer);
        // key的序列化采用StringRedisSerializer
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }


    @Bean
    public CacheManager cacheManager(@Autowired RedisConnectionFactory redisConnectionFactory) {
//        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
//
//        //默认超时时间,单位秒
//
//        cacheManager.setDefaultExpiration(3000);
//        //根据缓存名称设置超时时间,0为不超时
//        Map<String,Long> expires = new ConcurrentHashMap<>();
//        cacheManager.setExpires(expires);
//
//        return cacheManager;
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager
            .RedisCacheManagerBuilder
            .fromConnectionFactory(redisConnectionFactory);
        return builder.build();

    }
}
