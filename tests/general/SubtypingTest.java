package general;

import org.checkerframework.checker.linear.qual.NonLinear;
import org.checkerframework.checker.linear.qual.Unique;

class SubtypingTest {

    void test(@NonLinear String x, @Unique String y, @NonLinear String z) {
        @NonLinear String b;
        // :: error: unique.assignment.not.allowed
        b = y;
        // :: error: unique.assignment.not.allowed
        b = y;
        // test method invocation
        //        testInvocation(y);
    }

    //    void test2() {
    //        @Top String x = "a";
    //        @Top String y = "b";
    //        @Top String z = "c";
    //        test(x, y, z);
    //    }

    //    void testInvocation(String x) {
    //        x = "a";
    //        return;
    //    }

    //    private static class MyClass {
    //        //According to the type rules, should report an error here.
    //        @Unique x;
    //    }
}
