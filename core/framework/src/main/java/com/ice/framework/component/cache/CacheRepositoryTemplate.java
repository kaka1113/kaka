package com.ice.framework.component.cache;

import com.ice.framework.redis.BaseRedisDao;
import com.ice.framework.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author qiang.su
 * @since 2020/11/9
 */

@Slf4j
@Component
public class CacheRepositoryTemplate<T> {

    public static Integer SUCCESS = 0;
    public static Integer FAILED = -1;

//    @Resource
//    private EhCacheUtils ehCacheUtils;
    @Resource
    private BaseRedisDao<String, Object> baseRedisDao;


    public <T> CacheRepositoryTemplate() {
    }

    /**
     * 用于其他业务代码已经维护好数据update
     *
     * @param key  key
     * @param dbId db id
     * @return RepositoryResult
     */
    protected EhCacheUtils.RepositoryResult<T> queryCache(String key, Long dbId) {
        return queryCacheExpire(key, dbId, null, true);
    }

    /**
     * 用于其他业务代码已经维护好数据update
     *
     * @param key  key
     * @param dbId db id
     * @return RepositoryResult
     */
    protected EhCacheUtils.RepositoryResult<T> queryCacheRedisOnly(String key, Long dbId) {
        return queryCacheExpire(key, dbId, null, false);
    }

    /**
     * 无ehcache,用于时间较短没必要ehcache，或者自定义时间的数据
     *
     * @param key     key
     * @param dbId    db id
     * @param seconds seconds
     * @return RepositoryResult
     */
    protected EhCacheUtils.RepositoryResult<T> queryCacheRedisOnlyExpire(String key, Long dbId, Long seconds) {
        return queryCacheExpire(key, dbId, seconds, false);
    }

    /**
     * ehcache暂存60秒。数据查询频繁，又不太变动的数据
     *
     * @param key     key
     * @param dbId    db id
     * @param seconds seconds
     * @param ehcache ehcache
     * @return RepositoryResult
     */
    protected EhCacheUtils.RepositoryResult<T> queryCacheExpire(String key, Long dbId, Long seconds, boolean ehcache) {
        EhCacheUtils.RepositoryResult<T> repositoryResult = null;
        try {
            //from ehcache
//            if (ehcache) {
//                repositoryResult = ehCacheUtils.getCache(key);
//            }
            if (ObjectUtils.isEmpty(repositoryResult)) {
                Object item;
                //from redis
                item = baseRedisDao.get(key);
                if (null == item) {
                    //from db 需要业务override
                    item = queryFromDb(dbId);
                    if (item != null) {
                        //save redis
                        if (null == seconds || 0L == seconds) {
                            baseRedisDao.set(key, item);
                        } else {
                            baseRedisDao.set(key, item, seconds);
                        }
                    }
                }
                //save ehcache
                if (null != item) {
                    repositoryResult = setSuccess(item);
                } else {
                    repositoryResult = setSuccess(null);
                }
                if (ehcache) {
//                    ehCacheUtils.putCache(key, repositoryResult);
                }
            }
        } catch (Exception e) {
            log.info("CacheRepositoryTemplate.queryCache error", e);
            repositoryResult = setFailed("缓存查询异常！");
            if (ehcache) {
//                ehCacheUtils.putCache(key, repositoryResult);
            }
        }
        return repositoryResult;
    }

    /**
     * 查询对象
     */
    public Object queryFromDb(Long dbId) {
        return null;
    }




    public EhCacheUtils.RepositoryResult<T> setSuccess(Object item) {
        return new EhCacheUtils.RepositoryResult(CacheRepositoryTemplate.SUCCESS, item, null);
    }

    public EhCacheUtils.RepositoryResult<T> setFailed(String error) {
        return new EhCacheUtils.RepositoryResult(CacheRepositoryTemplate.FAILED, null, error);
    }
}
