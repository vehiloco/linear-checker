package general;

import org.checkerframework.checker.linear.qual.*;

public class NullTest {
    void test1() {
        Object o1 = null;
        Object o2 = o1;

        //        @Unique Object o3 = new Object();
        //        Object o4;
        //        o4 = o3;
        int a = 1;
        Integer b = a > 1 ? 0 : null;
    }
}
