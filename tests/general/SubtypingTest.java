package general;

import org.checkerframework.checker.linear.qual.NonLinear;
import org.checkerframework.checker.linear.qual.Unique;

class SubtypingTest {

    void test(@NonLinear String x, @Unique String y, @Unique({"algo1", "algo2"}) String z) {
        @NonLinear String a = x;
        @NonLinear String b;
        b = y;
        b = y;
        //        @Unique({"algo1", "algo2"})
        //        String e;
        //        e = z;
        //        MyClass m1 = new MyClass();
        //        testInvocation(e);
    }

    //    void test2() {
    //        @Top String x = "a";
    //        @Top String y = "b";
    //        @Top String z = "c";
    //        test(x, y, z);
    //    }

    //    void testInvocation(@Top String x) {
    //        String y;
    //        y = x;
    //    }

    //    private static class MyClass {
    //        void a(@Top String x) {
    //            String y;
    //            y = x;
    //        }
    //    }
}
