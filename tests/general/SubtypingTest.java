package general;

import org.checkerframework.checker.linear.qual.EnsureUnique;
import org.checkerframework.checker.linear.qual.MayAliased;
import org.checkerframework.checker.linear.qual.Unique;

class SubtypingTest {

    void test(@Unique({"a"}) String x, @Unique String y, @MayAliased String z) {
        @Unique String b;
        b = y;
        // ::error: unique.assignment.not.allowed
        b = y;
        // ::error: unique.parameter.not.allowed
        testInvocation(y);
        // ::error: unique.assignment.not.allowed
        b = y;
        @Unique({"a"})
        String bytesIV;
        bytesIV = x;
        nextBytesSimulator(bytesIV);
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

    void testInvocation(String x) {
        String b;
        b = x;
        return;
    }

    @EnsureUnique(
            value = "#1",
            states = {"a", "initialized"})
    void nextBytesSimulator(@Unique({"a"}) String str) {
        return;
    }
}
