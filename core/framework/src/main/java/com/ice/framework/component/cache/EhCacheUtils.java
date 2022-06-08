package com.ice.framework.component.cache;

import lombok.Data;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author qiang.su
 * @since 2020/11/9
 */
@Component
public class EhCacheUtils<T> {

//    @Resource
//    private CacheManager cacheManager;

//    public void putCache(String key, Object item) {
//        Cache<String, Object> cache = cacheManager.getCache("preConfigured", String.class, Object.class);
//        cache.put(key, item);
//    }
//
//    public RepositoryResult getCache(String key) {
//        Cache<String, Object> cache = cacheManager.getCache("preConfigured", String.class, Object.class);
//        return (RepositoryResult) cache.get(key);
//    }

    @Data
    public static  class RepositoryResult<T> {
        private Integer success;
        private T item;
        private String error;

        public RepositoryResult(Integer success, T item, String error) {
            this.success = success;
            this.item = item;
            this.error = error;
        }
    }

}
