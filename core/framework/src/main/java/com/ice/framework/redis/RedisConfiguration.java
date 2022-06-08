package com.ice.framework.redis;

import com.alibaba.fastjson.parser.ParserConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author hubo
 * @since 2020/2/25
 */
@Configuration
public class RedisConfiguration {

    @Bean
    public RedisSerializer fastJson2JsonRedisSerializer() {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        return new FastJson2JsonRedisSerializer<Object>(Object.class);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory, RedisSerializer fastJson2JsonRedisSerializer) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate(factory);
        redisTemplate.setHashValueSerializer(fastJson2JsonRedisSerializer);
        //StringRedisSerializer  key  序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //keySerializer  对key的默认序列化器。默认值是StringSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //  valueSerializer
        redisTemplate.setValueSerializer(fastJson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean("redisTemplateProd")
    public RedisTemplate<String, String> redisTemplateProd(RedisSerializer fastJson2JsonRedisSerializer) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName("101.132.41.134");
        configuration.setPort(6390);
        configuration.setPassword("GateonDmpPro");
        configuration.setDatabase(5);
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(configuration);
        connectionFactory.afterPropertiesSet();
        StringRedisTemplate redisTemplate = new StringRedisTemplate(connectionFactory);
        redisTemplate.setHashValueSerializer(fastJson2JsonRedisSerializer);
        //StringRedisSerializer  key  序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //keySerializer  对key的默认序列化器。默认值是StringSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //  valueSerializer
        redisTemplate.setValueSerializer(fastJson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }


}
