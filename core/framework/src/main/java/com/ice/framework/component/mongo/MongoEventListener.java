package com.ice.framework.component.mongo;

import com.ice.framework.annotation.AutoIncKey;
import com.ice.framework.util.DocNoUtil;
import com.ice.framework.util.ObjectUtils;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.util.ReflectionUtils;

/**
 * @author : tjq
 * @since : 2022/4/19 16:15
 */
@EnableMongoRepositories
public class MongoEventListener extends AbstractMongoEventListener<Object> {

    /**
     * 在 object 被MongoConverter转换为Document之前，在MongoTemplate insert，insertList和save操作中调用。
     *
     * @param event
     *
     */
    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        final Object source = event.getSource();
        if (ObjectUtils.isNotEmpty(source)) {
            ReflectionUtils.doWithFields(source.getClass(), field -> {
                ReflectionUtils.makeAccessible(field);
                if (field.isAnnotationPresent(AutoIncKey.class) /*&& field.get(source) instanceof Number && field.getLong(source) == 0*/) {
                    field.set(source, DocNoUtil.getNextDocNoBYSaas(DocNoUtil.DocTypeEnum.MONGO_ID_INCRI));
                }
            });
        }
    }
}



