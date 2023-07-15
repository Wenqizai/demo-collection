package com.wenqi.test.mybatis;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author liangwenqi
 * @date 2023/4/14
 */
public interface RoleMapper {
    @Select("select id, role_name as roleNaccme, note from role where id = #{id,jdbcType=BIGINT}")
    public Role getRole(Long id);

    public Role findRole(String roleName);

    public int deleteRole(Long id);

    public int insertRole(Role role);

    List<Role> selectRoleById(@Param("list") List<Long> ids);

}
