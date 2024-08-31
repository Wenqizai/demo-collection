package com.wenqi.springboot.center.transaction;

import com.wenqi.springboot.mapper.SpringBootRoleMapper;
import com.wenqi.springboot.pojo.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 测试事务是否生效
 * @author liangwenqi
 * @date 2024/1/15
 */
@Service
public class SpringTransactionEffect {
    @Autowired
    private SpringBootRoleMapper springBootRoleMapper;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Transactional
    public void testPrivateMethodWithAnno() {
        Long id = this.insertOneWithAnno("testPrivateMethodWithAnno1");
        this.deleteOneWithAnno(id);
    }

    public void testPrivateMethodWithTemplate() {
        transactionTemplate.execute(e -> {
            Long id = this.insertOne("testPrivateMethodWithTemplate");
            this.deleteOne(id);
            return 1;
        });
    }

    @Transactional
    public Long insertOneWithAnno(String testDesc) {
        Role role = new Role();
        role.setRoleName(testDesc);
        role.setNote(testDesc);
        springBootRoleMapper.insertRole(role);
        return role.getId();
    }

    @Transactional
    public void deleteOneWithAnno(Long id) {
        int row = springBootRoleMapper.deleteRole(id);
        if (row > 0) {
            throw new RuntimeException("need to rollback " + id);
        }
    }


    private Long insertOne(String testDesc) {
        Role role = new Role();
        role.setRoleName(testDesc);
        role.setNote(testDesc);
        springBootRoleMapper.insertRole(role);
        return role.getId();
    }

    private void deleteOne(Long id) {
        int row = springBootRoleMapper.deleteRole(id);
        if (row > 0) {
            throw new RuntimeException("need to rollback " + id);
        }
    }
}
