package general;

import org.checkerframework.checker.linear.qual.EnsureUnique;
import org.checkerframework.checker.linear.qual.MayAliased;
import org.checkerframework.checker.linear.qual.Unique;

class SubtypingTest {

    void test(@Unique({"initialized"}) String x, @Unique String y, @MayAliased String z) {
        @Unique String b;
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
        nextBytesSimulator(bytesIV);
        // ::error: unique.assignment.not.allowed
        bytesIV = x;
    }

    void testInvocation(String x) {
        String b;
        b = x;
        return;
    }

    @SuppressWarnings("contracts.postcondition.not.satisfied")
    @EnsureUnique(
            value = "#1",
            states = {"initialized"})
    void nextBytesSimulator(@Unique({}) String str) {
        return;
    }
}
