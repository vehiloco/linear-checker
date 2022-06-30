package general;

import org.checkerframework.checker.linear.qual.MayAliased;
import org.checkerframework.checker.linear.qual.Unique;

class SubtypingTest {

    // full states are [initialized, state2, state3, state4], after get all of this
    // it can do nothing with the security random.
    // For example:
    // default state is {}, {initialized} means cannot be intizalied again,
    // similarly, state2 means cannot be state2 agaiin
    void test(@Unique({}) String x, @Unique({}) String y, @MayAliased String z) {
        @Unique({})
        String b;
        b = y;
        // ::error: unique.assignment.not.allowed
        b = y;
        // ::error: unique.parameter.not.allowed
        testInvocation(y);
        // ::error: unique.assignment.not.allowed
        b = y;

        @Unique({})
        String bytesIV;
        bytesIV = x;
        // finish rule of field update
        FieldTest fieldTest = new FieldTest("a");
        @Unique({})
        String forField = bytesIV;
        fieldTest.a = forField;
        // ::error: unique.assignment.not.allowed
        fieldTest.a = forField;
    }

    void testInvocation(String x2) {
        String b2;
        b2 = x2;
        return;
    }

    class FieldTest {
        public String a;

        public FieldTest(String val) {
            this.a = val;
        }
    }
}
