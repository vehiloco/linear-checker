package general;

import org.checkerframework.checker.linear.qual.EnsureUnique;
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
        //        // ::error: unique.parameter.not.allowed
        //        testInvocation(y);
        // ::error: unique.assignment.not.allowed
        b = y;

        @Unique({})
        String bytesIV;
        bytesIV = x;

        // For example, all states are {"initialized", "state2", "end"}, make explict state machine.
        // Supertype is unique({})
        //  @Unique({}) becomes  @Unique({"initialized"}), it can go to state2 or end, not
        // initialized again
        nextBytesSimulator(bytesIV);
        //  @Unique({"initialized"}) becomes  @Unique({"initialized", "state2"}), it can only end
        state2(bytesIV);
        // @Unique({"initialized", "state2"}) becomes  @Unique({"initialized", "state2", "end"})
        end(bytesIV);
    }

    void testInvocation(String x2) {
        String b2;
        b2 = x2;
        return;
    }

    @SuppressWarnings("contracts.postcondition.not.satisfied")
    @EnsureUnique(
            value = "#1",
            states = {"initialized"})
    public void nextBytesSimulator(@Unique({}) String str) {
        return;
    }

    @SuppressWarnings("contracts.postcondition.not.satisfied")
    @EnsureUnique(
            value = "#1",
            states = {"initialized", "state2"})
    public void state2(@Unique({}) String str) {
        return;
    }

    @SuppressWarnings("contracts.postcondition.not.satisfied")
    @EnsureUnique(
            value = "#1",
            states = {"initialized", "state2", "end"})
    public void end(@Unique({}) String str) {
        return;
    }
}
