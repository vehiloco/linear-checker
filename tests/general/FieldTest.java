package general;

import org.checkerframework.checker.linear.qual.*;

public class FieldTest {
    @Shared Object field1;
    @Shared Object field2;
    @Shared Object field3;

    void test1() {
        FieldTest fieldTest = new FieldTest();
        // ::warning: (cast.unsafe.constructor.invocation)
        @Unique Object rhsValue = new @Unique({}) Object();
        // ::warning: (cast.unsafe.constructor.invocation)
        @Unique Object rhsValue2 = new @Unique({"a"}) Object();
        // ::warning: (cast.unsafe.constructor.invocation)
        @Unique Object rhsValue3 = new @Unique({"b"}) Object();
        @Shared Object rhsValue4 = new @Shared({"c"}) Object();
        // fieldTest.field1 still need to be @shared
        fieldTest.field1 = rhsValue;
        // field2 should be @Shared({"a"})
        fieldTest.field2 = rhsValue2;
        // field2 should be @Shared({"a", "b"})
        fieldTest.field2 = rhsValue3;
        // TODO: this test case failed
        fieldTest.field3 = rhsValue4;
    }
}
