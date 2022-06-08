package com.mg.framework.component.db;

import com.alibaba.druid.pool.DruidDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用到读写分离数据源请先测试、
 *
 * 说明：
 * 1. 首先需要配置好seata客户端
 * 2. 配置读写分离数据源
 * 3.测试基于mybatis/或者mybatisPlus的分布式事物是否能回滚
 *
 *
 * @author tjq
 * @since 2022/5/17 12:22
 */
@Configuration
@ConditionalOnProperty(name = "mg.db.rw.enabled", havingValue = "true", matchIfMissing = false)
public class DataSourceProxyConfig {

    @Bean("originMaster")
    @Primary
    @ConfigurationProperties(prefix = "mg.db.rw.master")
    public DataSource dataSourceMaster() {
        return new DruidDataSource();
    }

    @Bean("originSlaver")
    @ConfigurationProperties(prefix = "mg.db.rw.slaver")
    public DataSource dataSourceStorage() {
        return new DruidDataSource();
    }

    @Bean(name = "master")
    public DataSourceProxy masterDataSourceProxy(@Qualifier("originMaster") DataSource dataSource) {
        return new DataSourceProxy(dataSource);
    }

    @Bean(name = "slaver")
    public DataSourceProxy slaverDataSourceProxy(@Qualifier("originSlaver") DataSource dataSource) {
        return new DataSourceProxy(dataSource);
    }

    @Bean("dynamicDataSource")
    public DataSource dynamicDataSource(@Qualifier("master") DataSource dataSourceMaster,
                                        @Qualifier("slaver") DataSource dataSourceSlaver) {

        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>(2);
        dataSourceMap.put(DataSourceKey.MASTER.name(), dataSourceSlaver);
        dataSourceMap.put(DataSourceKey.SLAVER.name(), dataSourceMaster);


        dynamicRoutingDataSource.setDefaultTargetDataSource(dataSourceMaster);
        dynamicRoutingDataSource.setTargetDataSources(dataSourceMap);

        DynamicDataSourceContextHolder.getDataSourceKeys().addAll(dataSourceMap.keySet());

        return dynamicRoutingDataSource;
    }

    /**
     * Mybatis
     */
//    @Bean
//    @ConfigurationProperties(prefix = "mybatis")
//    public SqlSessionFactoryBean sqlSessionFactoryBean(@Qualifier("dynamicDataSource") DataSource dataSource) {
//        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(dataSource);
//        return sqlSessionFactoryBean;
//    }

    /**
     * Mybatis-plus
     *
     * @param dataSource
     * @return
     */
//    @Bean
//    @ConfigurationProperties(prefix = "mybatis-plus")
//    public MybatisSqlSessionFactoryBean sqlSessionFactoryBeanPlus(@Qualifier("dynamicDataSource") DataSource dataSource) {
//        // 这里用 MybatisSqlSessionFactoryBean 代替了 SqlSessionFactoryBean，否则 MyBatisPlus 不会生效
//        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
//        mybatisSqlSessionFactoryBean.setDataSource(dataSource);
//        return mybatisSqlSessionFactoryBean;
//    }
}
