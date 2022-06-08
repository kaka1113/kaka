package com.mg.framework.component.schema;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mg.framework.util.ObjectUtils;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自动DB生成脚本
 *
 * @author : tjq
 * @since : 2022/4/26 18:03
 */
public class AutoDBScript implements ApplicationContextAware {

    private Logger log = LoggerFactory.getLogger(AutoDBScript.class);

    @Value("${mg.db.database:#{null}}")
    private String dbName;

    @Value("${mg.db.scanPackage:#{null}}")
    private String scanPackage;

    @Resource
    private DataSource dataSource;


    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        // 扫描包中类型并记得标有 LanguageProvider 的接口类型
        List<Class<?>> interfaceTypes = getTableClass();
        Map<String, Set<String>> tableCol = new HashMap<>();
        for (Class<?> interfaceType : interfaceTypes) {
            //判断是否被标识为表结构
            boolean annotationPresent = interfaceType.isAnnotationPresent(TableName.class);
            if (!annotationPresent) {
                return;
            }
            //获取db表名称
            String tableName = ObjectUtils.toUnderlineCase(interfaceType.getSimpleName());
            //获取实体类的属性,需要先获取下父类得属性，然后子类才能加载到父类得属性
            Field[] declaredFields = interfaceType.getSuperclass().getDeclaredFields();
            Field[] fields = interfaceType.getDeclaredFields();
            List<Field> fieldList = new ArrayList<>(Arrays.asList(fields));
            if (ObjectUtils.isNotEmpty(declaredFields)) {
                fieldList.addAll(Arrays.asList(declaredFields));
            }
            //获取数据库表中column中的参数
            Set<String> columnSet = fieldList.stream().filter(field -> field.isAnnotationPresent(ApiModelProperty.class)).map(item -> {
                TableField tableField = item.getAnnotation(TableField.class);
                if (ObjectUtils.isNotEmpty(tableField) && !tableField.exist()) {
                    return null;
                }
                return ObjectUtils.toUnderlineCase(item.getName()).toLowerCase();
            }).collect(Collectors.toList()).stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toSet());
            tableCol.put(tableName, columnSet);
        }
        //校验数据库中的表和代码中的注释是否有该表等
        try {
//            DataSource dataSource = initDataSource();
            Schema schema = new Schema(dataSource);
//            schema.load();
            Database database = schema.getDatabase(dbName);
            if (ObjectUtils.isEmpty(database)) {
                log.error("获取database失败dbName:{}",dbName);
                return;
            }
            Map<String, Table> tableMap = database.getTableMap();
            //移除事务日志表
            tableMap.remove("undo_log");
            tableMap.forEach((key, value) -> {
                List<String> colList = value.getColList();
                List<String> entityList = ObjectUtils.isEmpty(tableCol.get(key)) ? null : new ArrayList<>(tableCol.get(key));
                if (ObjectUtils.isEmpty(entityList)) {
                    log.info("数据库有表在业务中未实现：{}", key);
                    return;
                }
                //检查代码中有的数据库没有的字段
                entityList.removeAll(colList);
                if (ObjectUtils.isNotEmpty(entityList)) {
                    log.info("数据库中缺少的字段,表:{},字段：{}", key, entityList.toString());
                }

                //校验数据库和代码中的实体注释是否一致，不一致打印更新sql
                //String format = String.format("alter table %s modify column  %s %s %s  comment '%s'", "tableName", name,
                //typeNameConvert(typeName), isNull ? "null" : "not null", apiModelProperty.value());
                //log.info("SQL：{}", format);
            });
            log.info("数据库表结构对比结束");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Class<?>> getTableClass() {
        List<Class<?>> interfaceTypes = new LinkedList<>();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter((metadataReader, metadataReaderFactory) -> {
            // 获取类型上的 LanguageProvider 注解
            Map<String, Object> annotationAttributes = metadataReader.getAnnotationMetadata().getAnnotationAttributes(TableName.class.getName());
            // 为空则跳过
            if (annotationAttributes == null)
                return false;
            Class<?> interfaceType = null;
            try {
                interfaceType = Class.forName(metadataReader.getClassMetadata().getClassName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            // 由于接口类型，因此仅返回 true 也不会得到，所以我们使用一个 List 类型，一般过滤一般加载
            interfaceTypes.add(interfaceType);
            return true;
        });
        scanner.findCandidateComponents(scanPackage);
        return interfaceTypes;
    }

//    private DataSource initDataSource() throws Exception {
//        Map<String, String> map = new HashMap<>();
//        map.put("driverClassName", "com.mysql.jdbc.Driver");
//        map.put("url", "jdbc:mysql://192.168.16.239:3306?useSSL=true&verifyServerCertificate=false");
//        map.put("username", "prod");
//        map.put("password", "MysqlDmpDev");
//        map.put("initialSize", "2");
//        map.put("maxActive", "2");
//        map.put("maxWait", "60000");
//        map.put("timeBetweenEvictionRunsMillis", "60000");
//        map.put("minEvictableIdleTimeMillis", "300000");
//        map.put("validationQuery", "SELECT 1 FROM DUAL");
//        map.put("testWhileIdle", "true");
//        return DruidDataSourceFactory.createDataSource(map);
//    }
}
