package com.wenqi.springboot.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * @author liangwenqi
 * @date 2023/8/15
 */
@Configuration
@MapperScan(basePackages = {"com.wenqi.springboot.mapper"}, sqlSessionFactoryRef = "sqlSessionFactory")
public class DataSourceConfig {

    private static final String MAPPER_LOCAL_EXT = "classpath:mapper/**/*.xml";

    @Value("${mysql.username}")
    private String username;
    @Value("${mysql.password}")
    private String password;
    @Value("${mysql.url}")
    private String url;

    @Bean(name = "dataSource")
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        // dataSource.setDriverClassName(driverClassName);//如果不配置druid会根据url自动识别dbType，然后选择相应的driverClassName
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setValidationQuery("SELECT 1");//用来检测连接是否有效
        dataSource.setTestOnBorrow(false);//借用连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        dataSource.setTestOnReturn(false);//归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        // 连接空闲时检测，如果连接空闲时间大于timeBetweenEvictionRunsMillis指定的毫秒，执行validationQuery指定的SQL来检测连接是否有效
        dataSource.setTestWhileIdle(true);//如果检测失败，则连接将被从池中去除
        dataSource.setTimeBetweenEvictionRunsMillis(60000);//1分钟
        dataSource.setMaxActive(20);
        dataSource.setInitialSize(5);
        return dataSource;
    }


    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        Resource[] resourcesExt = new PathMatchingResourcePatternResolver().getResources(MAPPER_LOCAL_EXT);
        bean.setMapperLocations(resourcesExt);
        bean.setDataSource(dataSource);
        return bean.getObject();
    }

    @Bean(name = "transactionManager")
    public DataSourceTransactionManager testTransactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = "transactionTemplate")
    public TransactionTemplate transactionTemplate(@Qualifier("transactionManager") PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

}
