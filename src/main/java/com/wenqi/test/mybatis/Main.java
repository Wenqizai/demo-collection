package com.wenqi.test.mybatis;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.RandomUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
//import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author liangwenqi
 * @date 2023/4/14
 */
public class Main {
  public static void main(String[] args) {
    // 加了这一句log4j才生效
    //BasicConfigurator.configure();
    //PropertyConfigurator.configure("src/main/resources/mybatis/log4j.properties.bk");

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

      // 业务方法
//      test01(sqlSession);
//      testInsert(sqlSession);
//      testDtoSelect(sqlSession);
//      testCondition(sqlSession);
      executeForMap(sqlSession);

      
      sqlSession.commit();

    } catch (Exception e) {
      // TODO Auto-generated catch block
      sqlSession.rollback();
      e.printStackTrace();
    } finally {
      sqlSession.close();
    }
  }

  private static void testCondition(SqlSession sqlSession) {
    RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
    RoleConditionDto roleConditionDto = new RoleConditionDto();
    roleConditionDto.setNoteCondition(1);
    roleMapper.selectByCondition(roleConditionDto);
  }

  private static void testBatch(SqlSession sqlSession) {
    RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
    List<Role> roleList = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      Role role = new Role();
      role.setRoleName("张-" + i);
      role.setNote("test insert " + i);
      roleList.add(role);
    }
    roleMapper.batchInsert(roleList);
    roleMapper.selectByIds(Arrays.asList(1L, 2L, 3L));
  }

  private static void testDtoSelect(SqlSession sqlSession) {
    RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
    Role role = new Role();
    role.setRoleName("张");
    role.setNote("AND note = 'test select key'");
    roleMapper.selectByRole(role);
  }

  private static void testInsert(SqlSession sqlSession) {
    RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
    Role role = new Role();
    role.setRoleName("张山-" + RandomUtils.nextInt(0, 1000));
    role.setNote("test select key");
    roleMapper.insertRole(role);
  }

  /**
   * 模拟in参数过大会诱发oom场景
   */
  private static void test02(SqlSession sqlSession) {
    RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
    List<Role> roles = roleMapper.selectRoleById(Arrays.asList(1L, 2L, 3L, 4L));
    System.out.println(JSON.toJSONString(roles));
  }

  private static void test01(SqlSession sqlSession) {
    RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
    Role role = roleMapper.getRole(1L);
    System.out.println(role.getId() + ":" + role.getRoleName() + ":" + role.getNote());
  }

  private static void executeForMap(SqlSession sqlSession) {
    RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
    Map<Long, Role> stringRoleMap = roleMapper.selectMapByIds(Arrays.asList(1L, 2L, 3L));
    System.out.println(stringRoleMap);
  }

}
