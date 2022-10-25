package com.wenqi.example.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author liangwenqi
 * @date 2022/10/25
 */
public class GroupByTest {
    public static void main(String[] args) {
        List<User> users = Arrays.asList(new User("a", 1), new User("b", 2), new User(null, 3));
        Map<String, List<User>> map = users.stream().collect(Collectors.groupingBy(x -> Optional.ofNullable(x.getName()).orElse("")));
        System.out.println(map);
    }

    public static class User {
        private String name;
        private Integer age;

        public User(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }
}
