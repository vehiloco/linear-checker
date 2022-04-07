package general;

import org.checkerframework.checker.crypto.qual.Top;
import org.checkerframework.checker.crypto.qual.Unique;

class SubtypingTest {

    // Here we just want to test the subtyping rules, normally we should not use
    // @UnknownAlgorithmOrProvider and @AllowedAlgorithms in this way.
    void test(@Top String x, @Unique String y, @Unique({"algo1", "algo2"}) String z) {
        @Top String a = x;
        @Top String b;
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
