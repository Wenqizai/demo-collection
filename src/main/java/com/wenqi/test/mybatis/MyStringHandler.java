package com.wenqi.test.mybatis;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author liangwenqi
 * @date 2023/4/14
 */
@MappedTypes({String.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class MyStringHandler implements TypeHandler<String> {
  private Logger log = LoggerFactory.getLogger(MyStringHandler.class);

  @Override
  public String getResult(ResultSet rs, String colName) throws SQLException {
    log.info("使用我的TypeHandler,ResultSet列名获取字符串");
    return rs.getString(colName);
  }

  @Override
  public String getResult(ResultSet rs, int index) throws SQLException {
    log.info("使用我的TypeHandler,ResultSet下标获取字符串");
    return rs.getString(index);
  }

  @Override
  public String getResult(CallableStatement cs, int index) throws SQLException {
    log.info("使用我的TypeHandler,CallableStatement下标获取字符串");
    return cs.getString(index);
  }

  @Override
  public void setParameter(PreparedStatement ps, int index, String value, JdbcType arg3) throws SQLException {
    log.info("使用我的TypeHandler");
    ps.setString(index, value);
  }

}
