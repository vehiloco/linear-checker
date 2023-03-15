package general;

import org.checkerframework.checker.linear.qual.*;

class SubtypingTest {

    @Shared String f;
    // :: error: (field.type.incompatible)
    @Unique String u;

    void testAssignment(
            @Unique({"a"}) String a,
            @Unique({"b"}) String b,
            @Shared({"c"}) String c,
            @Shared({"d"}) String d) {
        // T-assign-shared, shared should be @Shared({"c"}) after being assignment;
        @Shared({})
        String shared;
        shared = c;
        // TODO: T-assign-shared, should shared be @Shared({"c", "d"})?
        shared = d;
        @Unique String unique;
        // :: error: (assignment.type.incompatible)
        unique = c;
        unique = a;
        // :: error: (disappear.assignment.not.allowed)
        unique = a;
        // :: error: (assignment.type.incompatible)
        unique = b;
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
        String r;
        r = this.invocation(y);
        // :: error: (disappear.arg.not.allowed)
        r = this.invocation(x);
    }

    String invocation(String a) {
        return a;
    }

    //    void testCommon(
    //            @Unique({"a"}) String a,
    //            @Unique({"b"}) String b,
    //            @Shared({"c"}) String c,
    //            @Shared({"d"}) String d) {
    //        // Test @Unique
    //        @Unique({})
    //        String x;
    //        // allowed
    //        x = a;
    //        // test reflextivity
    //        @Unique({"a"})
    //        String y;
    //        y = x;
    //        // not allowed
    //        // ::error: (assignment.type.incompatible)
    //        y = b;
    //        // Test @Shared
    //        // allowed
    //        c = y;
    //        // alowed
    //        c = d;
    //    }
    //
    //    void test(@Unique({}) String x, @Unique({"initialized"}) String y, @Shared String z) {
    //        String a;
    //        a = null;
    //        @Unique({})
    //        String b;
    //        b = y;
    //        // ::error: (disappear.assignment.not.allowed)
    //        b = y;
    //        //        testInvocation(b);
    //        //        // TODO: think about a new error key
    //        //        // ::error: (unique.parameter.not.allowed)
    //        //        testInvocation(y);
    //        // ::error: (disappear.assignment.not.allowed)
    //        b = y;
    //
    //        @Unique({})
    //        String bytesIV;
    //        bytesIV = x;
    //    }
    //
    //    void testInvocation(@Unique({"initialized"}) String x2) {
    //        String b2;
    //        b2 = x2;
    //        return;
    //    }
    //
    //    // ::error: disappear.parameter.not.allowed
    //    void testParameter(@Disappear Object o) {
    //        return;
    //    }
    //
    //    @Disappear
    //    // ::error: disappear.return.not.allowed
    //    Object testReturn(Object o) {
    //        // ::error: return.type.incompatible
    //        return o;
    //    }
    //
    //    // test states transfer between unique reference.
    //    void testStatesTransferUnique(@Unique({"initialized"}) Object x, @Unique({"used"}) Object
    // y) {
    //        @Unique({})
    //        Object z;
    //        // z is supposed to be Unique({"initialized"})
    //        z = x;
    //        // :: error: (disappear.assignment.not.allowed)
    //        z = x;
    //        // z is supposed to be Unique({"used"})
    //        z = y;
    //
    //        @Unique({"initialized"})
    //        Object n;
    //        // ::error: (assignment.type.incompatible)
    //        n = z;
    //    }
    //
    //    // test states transfer between shared
    //    void testSharedTransfer(@Shared({"x"}) Object x, @Shared({"x, y"}) Object y) {
    //        @Shared Object z;
    //        z = x;
    //        z = x;
    //        z = y;
    //    }
    //
    //    // test states transfer between shared and unique
    //    void testSharedUniqueTransfer(@Unique({"a"}) Object u1, @Unique({"b"}) Object u2) {
    //        @Shared Object s;
    //        // s is supposed to be @Unique({"a"})
    //        s = u1;
    //        // s is supposed to be @Unique({"a", "b"})
    //        s = u2;
    //    }
}
