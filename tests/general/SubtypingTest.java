package general;

import org.checkerframework.checker.linear.qual.MayAliased;
import org.checkerframework.checker.linear.qual.Unique;

class SubtypingTest {

    void test(@MayAliased String x, @Unique String y, @MayAliased String z) {
        @Unique String b;
        // Postcondition y is usedup
        b = y;
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
}
