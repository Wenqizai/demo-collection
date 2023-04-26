package mybatis;

import com.wenqi.test.mybatis.Role;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

/**
 * @author liangwenqi
 * @date 2023/4/26
 */
class ReflectorTest {

    @Test
    void testGetSetterType1() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Reflector reflector = reflectorFactory.findForClass(Role.class);
        // 默认构造器是空参构造器, 意味着需要调用 getDefaultConstructor 方法时, Role必须指定空仓构造器
        Constructor<?> defaultConstructor = reflector.getDefaultConstructor();
    }

}
