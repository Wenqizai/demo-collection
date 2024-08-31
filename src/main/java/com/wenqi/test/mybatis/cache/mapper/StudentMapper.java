package com.wenqi.test.mybatis.cache.mapper;

import com.wenqi.test.mybatis.cache.entity.StudentEntity;
import org.apache.ibatis.annotations.Param;

/**
 * @author Wenqi Liang
 * @date 2023/6/17
 */
public interface StudentMapper {
    public StudentEntity getStudentById(int id);

    public int addStudent(StudentEntity student);

    public int updateStudentName(@Param("name") String name, @Param("id") int id);

    public StudentEntity getStudentByIdWithClassInfo(int id);

}
