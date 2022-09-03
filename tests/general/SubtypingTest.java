package general;

import org.checkerframework.checker.linear.qual.Disappear;
import org.checkerframework.checker.linear.qual.Shared;
import org.checkerframework.checker.linear.qual.Unique;

class SubtypingTest {

    // full states are [initialized, state2, state3, state4], after get all of this
    // it can do nothing with the security random.
    // For example:
    // default state is {}, {initialized} means cannot be intizalied again,
    // similarly, state2 means cannot be state2 again
    void test(@Unique({}) String x, @Unique({"initialized"}) String y, @Shared String z) {
        String a;
        a = null;
        @Unique({})
        String b;
        b = y;
        // ::error: (disappear.assignment.not.allowed)
        b = y;
        //        // ::error: (unique.parameter.not.allowed)
        //        testInvocation(y);
        // ::error: (disappear.assignment.not.allowed)
        b = y;

        @Unique({})
        String bytesIV;
        bytesIV = x;
        // finish rule of field update
        FieldTest fieldTest = new FieldTest("a");
        @Unique({})
        String forField = bytesIV;
        fieldTest.a = forField;
        // ::error: disappear.assignment.not.allowed
        fieldTest.a = forField;
    }

    void testInvocation(String x2) {
        String b2;
        b2 = x2;
        return;
    }

    // ::error: disappear.parameter.not.allowed
    void testParameter(@Disappear Object o) {
        return;
    }

    @Disappear
    // ::error: disappear.return.not.allowed
    Object testReturn(Object o) {
        // ::error: return.type.incompatible
        return o;
    }

    // test states transfer between unique reference.
    void testStatesTransferUnique(@Unique({"a"}) Object x, @Unique({"b"}) Object y) {
        @Unique Object z;
        // z is supposed to be Unique({"a"})
        z = x;
        // z is supposed to be Unique({"b"})
        z = y;
    }

    // test states transfer between shared

    // test states transfer between shared and unique

    class FieldTest {
        public String a;

        public FieldTest(String val) {
            this.a = val;
        }
    }
}
