package springboot.center.transaction;

import com.wenqi.springboot.Application;
import com.wenqi.springboot.center.transaction.SpringTransactionEffect;
import com.wenqi.springboot.mapper.SpringBootRoleMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author liangwenqi
 * @date 2024/1/15
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ApplicationTest {

    @Autowired
    private SpringTransactionEffect springTransactionEffect;

    @Test
    public void test() {
        try {
            springTransactionEffect.testPrivateMethodWithAnno();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("##########################");
        try {
            //springTransactionEffect.testPrivateMethodWithTemplate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
