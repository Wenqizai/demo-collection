package springboot;

import com.wenqi.springboot.Application;
import com.wenqi.springboot.mapper.SpringBootRoleMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ApplicationTest {

    @Autowired
    private SpringBootRoleMapper springBootRoleMapper;

    @Test
    public void test() {
        System.out.println(springBootRoleMapper.getRole(1L));
    }

}

