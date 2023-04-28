package general;

import org.checkerframework.checker.linear.qual.*;

class SubtypingTest {

    @Shared String f;

    void testAssignment(
            @Unique({"a"}) String a,
            @Unique({"b"}) String b,
            @Shared({"c"}) String c,
            @Shared({"d"}) String d) {
        // T-assign-shared, shared should be @Shared({"c"}) after being assignment;
        @Shared({})
        String shared;
        shared = c;
        // T-assign-shared, should shared be @Shared({"c", "d"})?
        shared = d;
        @Unique String unique;
        // :: error: (assignment.type.incompatible)
        unique = c;
        unique = a;
        // :: error: (disappear.assignment.not.allowed)
        unique = a;
        // T-assign-U Unique to Shared, TODO: why unique to shared we keep all states?
        shared = unique;
    }

    void testFieldUpdate(@Unique({"a"}) String a, @Shared({"c"}) String c) {
        // T-update-u, f shoulle be @shared({"a"})
        this.f = a;
        // :: error: (disappear.assignment.not.allowed)
        this.f = a;
        // TODO: T-update-shared, should shared be @Shared({"c", "d"})?
        this.f = c;
        @Shared String shared;
        shared = this.f;
    }

    @Disappear
    // :: error: disappear.parameter.not.allowed
    // :: error: disappear.return.not.allowed
    String testMethodDeclaration(@Disappear String a) {
        return a;
    }

    void testMethodInvocation(@Unique String x) {
        @Unique String y;
        y = x;
        String r1;
        r1 = this.invocation(y);
        // :: error: (disappear.arg.not.allowed)
        r1 = this.invocation(x);
        @Unique String r2;
        // :: error: (assignment.type.incompatible)
        r2 = this.invocation("string");
    }

    @Shared
    String invocation(String a) {
        return a;
    }
}
