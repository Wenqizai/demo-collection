package com.wenqi.test.mybatis;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author liangwenqi
 * @date 2023/4/14
 */
public interface RoleMapper {
    //    @Select("select id, role_name as roleNaccme, note from role where id = #{id,jdbcType=BIGINT}")
    public Role getRole(Long id);

    public Role findRole(String roleName);

    public int deleteRole(Long id);

    public int insertRole(Role role);

    List<Role> selectRoleById(@Param("list") List<Long> ids);

    List<Role> selectByRole(Role role);

    Integer updateByRole(Role role);

    Integer batchInsert(@Param("roleList") List<Role> roleList);

    List<Role> selectByIds(@Param("idsList") List<Long> idsList);

    List<Role> selectByCondition(RoleConditionDto roleConditionDto);

    @MapKey("id")
    Map<Long, Role> selectMapByIds(List<Long> list);
}
