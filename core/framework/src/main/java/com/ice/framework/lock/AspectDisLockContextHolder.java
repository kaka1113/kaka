package com.ice.framework.lock;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Author: qiang.su
 * Date: 2021/5/26
 * Msg: 线程上下文持有
 */
@Slf4j
public class AspectDisLockContextHolder {

    //有序的锁set
    private static final ThreadLocal<LinkedHashSet<KeyContent>> lockKeys = ThreadLocal.withInitial(() ->
            new LinkedHashSet()
    );

    private static final ThreadLocal<Boolean> topLockFlag = ThreadLocal.withInitial(() ->
            new Boolean(false)
    );

    /**
     * @Author: qiang.su
     * @since: 2021/6/11 16:01
     * @Desc: 手动维护线锁的时候，要维护新的
     */
    public static synchronized CheckLockCallback checkTopLock() {
        Boolean aBoolean = topLockFlag.get();
        CheckLockCallback checkLockResult = new CheckLockCallback();
        if (!aBoolean) {
            topLockFlag.set(new Boolean(true));
            checkLockResult.setCanUnlock(true);
        } else {
            checkLockResult.setCanUnlock(false);
        }
        return checkLockResult;
    }

    //get key
    public static KeyContent getExist(String key) {
        LinkedHashSet<KeyContent> keys = lockKeys.get();
        for (KeyContent keyContent : keys) {
            if (keyContent.getKey().equals(key)) {
                return keyContent;
            }
        }
        return null;
    }

    //添加key
    public static void setKeyIfAbsent(KeyContent key) {
        LinkedHashSet<KeyContent> keys = lockKeys.get();
        if (!keys.contains(key)) {
            keys.add(key);
            System.out.println("add lock: " + key);
        }
    }

    //获取(逆序)keys用来释放
    public static LinkedHashSet<KeyContent> getKeysReversed() {
        LinkedHashSet<KeyContent> keys = lockKeys.get();
        if (keys.size() == 0) {
            return keys;
        }
        LinkedList<KeyContent> list = new LinkedList<>(keys);
        Iterator<KeyContent> itr = list.descendingIterator();

        LinkedHashSet<KeyContent> reversedKeys = new LinkedHashSet();
        while (itr.hasNext()) {
            KeyContent value = itr.next();
            reversedKeys.add(value);
        }
        return reversedKeys;
    }

    //获取是否有过标签
    public static boolean isEmpty() {
        LinkedHashSet<KeyContent> keyContents = lockKeys.get();
        log.info("lock context:{}", JSONObject.toJSONString(keyContents));
        return lockKeys.get().size() == 0;
    }

    /**
     * 要释放资源，threadlocal线程复用
     */
    public static void clean() {
        lockKeys.remove();
        topLockFlag.remove();
    }

    @Data
    public static class CheckLockCallback {
        private boolean canUnlock = false;
    }

    @Data
    public static class KeyContent {
        private String key;
        private RLock lock;
        private Integer expireTIme;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            KeyContent that = (KeyContent) o;
            return Objects.equals(key, that.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }

}
