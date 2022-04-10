package general;

import org.checkerframework.checker.linear.qual.NonLinear;
import org.checkerframework.checker.linear.qual.Unique;

class SubtypingTest {

    void test(@NonLinear String x, @Unique String y, @NonLinear String z) {
        //        @NonLinear String a = x;
        @NonLinear String b;
        // Record result here
        // 1. first round , (z = y) is rhs, if z is NonLinear and y is unique
        // then: rhs is unique, lhs b is nonlinear
        // 2. next, z = y, lhs @Nonlinear z, rhs y is @unique
        // then we let b = (y = z), i think we use a lub here, because rhs is unique
        b = (z = y);

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
