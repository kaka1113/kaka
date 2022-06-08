package com.mg.framework.component.db;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tjq
 * @since 2022/5/17 12:20
 */
public class DynamicDataSourceContextHolder {

    private static final ThreadLocal<String> CONTEXT_HOLDER = ThreadLocal.withInitial(DataSourceKey.MASTER::name);

    private static List<Object> dataSourceKeys = new ArrayList<>();

    public static void setDataSourceKey(DataSourceKey key) {
        CONTEXT_HOLDER.set(key.name());
    }

    public static String getDataSourceKey() {
        return CONTEXT_HOLDER.get();
    }

    public static void clearDataSourceKey() {
        CONTEXT_HOLDER.remove();
    }

    public static List<Object> getDataSourceKeys() {
        return dataSourceKeys;
    }

}
