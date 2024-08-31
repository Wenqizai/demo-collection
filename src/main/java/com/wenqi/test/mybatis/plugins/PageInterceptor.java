package com.wenqi.test.mybatis.plugins;

import joptsimple.internal.Strings;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 使用 MyBatis 插件实现分页
 *
 * @author liangwenqi
 * @date 2023/11/13
 */
@Intercepts({@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class PageInterceptor implements Interceptor {

    /**
     * 记录 Executor.query() 方法中, 指定类型参数在参数列表中的索引位置
     * MappedStatement 对象在参数列表中索引位置
     */
    private static int MAPPEDSTATEMENT_INDEX = 0;
    /**
     * 用户传入的实参对象在参数列表中的索引位置
     */
    private static int PARAMENTEROBJECT_INDEX = 1;
    /**
     * MappedStatement 类型的参数在参数内标中的索引位置
     */
    private static int ROWBOUNDS_INDEX = 2;
    /**
     * Dialect 对象, 对应每个数据库产品
     * <p>
     * 下面是 MySQL 中通过 limit 实现分页的 SQL 语句
     * select from t user limit 10,10
     * <p>
     * 下面是 Oracle 中通过 ROWNUM 实现分页的 SQL 语句
     * SELECT FROM (
     * SELECT u.*, ROWNUM rn FROM (SELECT FROM t user) u WHERE ROWNUM <= 20
     * )
     * WHERE rn > 10
     */
    private Dialect dialect;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 从 Invocation 对象中获取被拦截的方法的参数列表, 这里就是 Executor.query(MappedStatement, Object, RowBounds, ResultHandler) 的参数列表
        final Object[] queryArgs = invocation.getArgs();
        // 结合前面介绍的 PageInterceptor 中的字段, 获取 Executor.query() 方法中的参数
        // 获取 MappedStatement 对象
        final MappedStatement mappedStatement = (MappedStatement) queryArgs[MAPPEDSTATEMENT_INDEX];
        // 获取用户传入的实参对象
        final Object parameter = queryArgs[PARAMENTEROBJECT_INDEX];
        // 获取 RowBounds 对象
        final RowBounds rowBounds = (RowBounds) queryArgs[ROWBOUNDS_INDEX];

        // 获取 RowBounds 对象中记录的 offset 值, 也就是查询的起始位置
        int offset = rowBounds.getOffset();
        // 获取 RowBounds 对象中记录的 limit 值, 也就是查询返回的记录条数
        int limit = rowBounds.getLimit();

        // 获取 BoundSql 对象, 其中记录了包含"?"占位符的 SQL 语句
        final BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        // 获取 BoundSql 中记录的 SQL 语句
        final StringBuffer bufferSql = new StringBuffer(boundSql.getSql().trim());

        // 对 SQL 语句进行格式化, 在映射配置文件中编写 SQL 语句时, 或是经过动态 SQL 解析之后, SQL 语句的格式比较凌乱. 这里可以对 SQL 语句进行格式化
        String sql = getFormatSql(bufferSql.toString().trim());

        // 通过 Dialect 策略, 检测当前使用的数据库产品是否支持分页功能
        if (dialect.supportPage()) {
            // Dialect 策略根据具体的数据块产品, SQL 语句以及 offset 值和 limit 值, 生成包含分页功能的 SQL 语句
            sql = dialect.getPagingSql(sql, offset, limit);
            // 当前拦截的 Executor.query() 方法中的 RowBounds 参数不在控制查找结果集的范围, 所以要进行重置
            queryArgs[ROWBOUNDS_INDEX] = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
        }

        // 根据当前的 SQL 语句创建新的 MappedStatement 对象, 并更新到 Invocation 对象记录的参数列表中
        queryArgs[MAPPEDSTATEMENT_INDEX] = createMappedStatement(mappedStatement, boundSql, sql, parameter);

        // 通过Invocation.proceed() 方法调用被拦截的 Executor.query() 方法
        return invocation.proceed();
    }

    private Object createMappedStatement(MappedStatement mappedStatement, BoundSql boundSql, String sql, Object parameter) {
        // 为处理后的 SQL 语句创建新的 BoundSql 对象, 其中会复制原有 BoundSql 对象的 parameterMappings 等集合的信息
        BoundSql newBoundSql = createBoundSql(mappedStatement, boundSql, sql, parameter);
        // 为处理后的 SQL 语句创建新的 MappedStatement 对象, 其中封装的 BoundSql 是上面新建的 BoundSql 对象, 其他的字段直接复制原有的 MappedStatement 对象
        return buildMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));
    }

    private Object buildMappedStatement(MappedStatement ms, BoundSqlSqlSource boundSqlSqlSource) {
        MappedStatement.Builder statementBuilder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), boundSqlSqlSource, ms.getSqlCommandType()).resource(ms.getResource()).fetchSize(ms.getFetchSize()).timeout(ms.getTimeout()).statementType(ms.getStatementType()).keyGenerator(ms.getKeyGenerator()).keyProperty(Strings.join(ms.getKeyProperties(), ",")).keyColumn(Strings.join(ms.getKeyColumns(), ",")).databaseId(ms.getDatabaseId()).lang(ms.getLang()).resultOrdered(ms.isResultOrdered()).resultSets(Strings.join(ms.getResultSets(), ",")).resultMaps(ms.getResultMaps()).resultSetType(ms.getResultSetType()).flushCacheRequired(ms.isFlushCacheRequired()).useCache(ms.isUseCache()).cache(ms.getCache()).parameterMap(ms.getParameterMap());
        return statementBuilder.build();
    }

    private BoundSql createBoundSql(MappedStatement mappedStatement, BoundSql boundSql, String sql, Object parameter) {
        return new BoundSql(mappedStatement.getConfiguration(), sql, boundSql.getParameterMappings(), parameter);
    }

    private String getFormatSql(String sql) {
        return null;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * mybatis-config.xml 配置
     * <plugins>
     *      <plugin interceptor="com.xxx.interceptor.PageInterceptor">
     *          <property name="jdbc.dbType"value="mysql"/>
     *          <property name="Dialect.oracle"value="com.xxx.dialect.OracleDialect"/>
     *          <property name="Dialect.mysql"value="com.xxx.dialect.MySQLDialect"/>
     *          <property name="Dialect.mssql"value="com.xxx.dialect.SQLServerDialect"/>
     *      </plugin>
     * </plugins>
     */
    @Override
    public void setProperties(Properties properties) {
        //查找名称为"dbName"的配置项
        String dbName = properties.getProperty("dbName");
        //查找以"dialect."开头的配置项
        String prefix = "dialect.";
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = (String) entry.getKey();
            if (key != null && key.startsWith(prefix)) {
                result.put(key.substring(prefix.length()), (String) entry.getValue());
            }
        }
        //获取当前使用的数据库产品对应的Dialect对象
        String dialectClass = result.get(dbName);
        try {
            //通过反射的方式创建Dialect接口的具体实现
            Dialect dialect = (Dialect) Class.forName(dialectClass).newInstance();
            //设置当前使用的Dialect策略
            this.setDialect(dialect);
        } catch (Exception e) {
            throw new RuntimeException("Can't find Dialect for " + dbName + "!", e);
        }
    }

    private void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }
}