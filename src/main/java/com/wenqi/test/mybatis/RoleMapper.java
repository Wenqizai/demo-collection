package com.wenqi.test.mybatis;

/**
 * @author liangwenqi
 * @date 2023/4/14
 */
public interface RoleMapper {
    public Role getRole(Long id);

    public Role findRole(String roleName);

    public int deleteRole(Long id);

    public int insertRole(Role role);
}
