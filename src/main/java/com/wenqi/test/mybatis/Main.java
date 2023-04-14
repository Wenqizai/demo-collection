package com.wenqi.test.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author liangwenqi
 * @date 2023/4/14
 */
public class Main {
  public static void main(String[] args) {
    String resource = "mybatis/mybatis-config.xml";
    InputStream inputStream = null;
    try {
      inputStream = Resources.getResourceAsStream(resource);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    SqlSessionFactory sqlSessionFactory = null;
    sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    SqlSession sqlSession = null;
    try {
      sqlSession = sqlSessionFactory.openSession();
      RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
      Role role = roleMapper.getRole(1L);
      System.out.println(role.getId() + ":" + role.getRoleName() + ":" + role.getNote());
      sqlSession.commit();

    } catch (Exception e) {
      // TODO Auto-generated catch block
      sqlSession.rollback();
      e.printStackTrace();
    } finally {
      sqlSession.close();
    }
  }
}
