// package general;
//
// import java.security.SecureRandom;
// import org.checkerframework.checker.linear.qual.Unique;
//
// class EnsureUniqueTest {
//    // For example, suppose state0 is a initial state and will be changed
//    // to state1 after being called by test1, while test 2 only accepts
//    // state0.
//    // One question is should it be only used on method parameter?
//    // multi args mean all allowed
//    // change the postcondition into @top and try to show some msg.
//    // relation between top and mayalias
//    public void test1() {
//        @Unique({})
//        byte[] bytesIV = new byte[16];
//        SecureRandom secureRandom = new SecureRandom();
//        secureRandom.nextBytes(bytesIV);
//    }
//    //    @EnsureUnique(value = "#1", state = "state1")
//    //    void test1(@Unique({"state0"}) String x, @Unique String y) {}
//    //
//    //    @EnsureUnique(value = "#1", state = "state1")
//    //    //    @RequireUnique(value = "#1", value = "state0"), dont really need, extra
// requirement
//    // on
//    //    // parameters
//    //    void test2(@Unique({"state0"}) String x, @Unique String y) {}
// }
