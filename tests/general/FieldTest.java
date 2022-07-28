package general;

import org.checkerframework.checker.linear.qual.Unique;

public class FieldTest {
    Object field1;
    Object field2;

    void test1() {
        FieldTest fieldTest = new FieldTest();
        // ::warning: (cast.unsafe.constructor.invocation)
        @Unique Object rhsValue = new @Unique Object();
        fieldTest.field1 = rhsValue;
        // ::error: disappear.assignment.not.allowed
        fieldTest.field2 = rhsValue;
        fieldTest.field1 = fieldTest.field2;
        fieldTest.field2 = fieldTest.field1;
    }
}
