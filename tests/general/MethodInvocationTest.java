package general;

import org.checkerframework.checker.linear.qual.Shared;
import org.checkerframework.checker.linear.qual.Unique;

public class MethodInvocationTest {
    public @Unique Object unique() {
        // ::warning: (cast.unsafe.constructor.invocation)
        return new @Unique Object();
    }

    public @Shared Object shared() {
        return new @Shared Object();
    }

    public void test1() {
        @Unique Object o1;
        @Shared Object o2;
        MethodInvocationTest mit = new MethodInvocationTest();
        o2 = mit.unique();
        o2 = mit.shared();
        o1 = mit.unique();
        // :: error: (assignment.type.incompatible)
        o1 = mit.shared();
    }
}
