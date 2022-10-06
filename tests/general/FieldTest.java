package general;

import org.checkerframework.checker.linear.qual.*;

public class FieldTest {
    @Shared Object field1;
    @Shared Object field2;
    @Shared Object field3;

    void test1() {
        FieldTest fieldTest = new FieldTest();
        // ::warning: (cast.unsafe.constructor.invocation)
        @Unique Object rhsValue = new @Unique Object();
        @Unique Object rhsValue2 = new @Unique({"a"}) Object();
        @Shared Object rhsValue3 = new @Shared({"b"}) Object();
        // fieldTest.field1 still need to be @shared
        fieldTest.field1 = rhsValue;
        // TODO: this test case failed, field2 should be shared("a")
        fieldTest.field2 = rhsValue2;
        // TODO: this test case failed
        fieldTest.field3 = rhsValue3;
    }
}
