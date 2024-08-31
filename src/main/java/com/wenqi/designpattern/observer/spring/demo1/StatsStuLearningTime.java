package com.wenqi.designpattern.observer.spring.demo1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StatsStuLearningTime {

    /**
     * 班级id
     */
    private String classId;

    /**
     * 课次序号
     */
    private Integer cucNo;
}
