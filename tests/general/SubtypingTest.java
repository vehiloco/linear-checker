package general;

import org.checkerframework.checker.linear.qual.NonLinear;
import org.checkerframework.checker.linear.qual.Unique;
import org.checkerframework.checker.linear.qual.UsedUp;

class SubtypingTest {

    void test(@NonLinear String x, @Unique String y, @UsedUp String z) {
        @NonLinear String b;
        // :: error: assignment.type.incompatible
        b = y;
        // :: error: assignment.type.incompatible
        b = y;
        // test method invocation
        //        testInvocation(y);

    }

    //    @Unique String id(@Unique -> @Top String x) {
    //        @Unique String ret = x;
    //        return ret;
    //    }
    //
    //    void testId() {
    //        @Unique String w = "alex";
    //        @Unique String u;
    //        u = id(w);
    //        u = w; // should fail
    //    }

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
