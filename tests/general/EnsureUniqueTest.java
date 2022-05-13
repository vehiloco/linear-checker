package general;

import java.security.SecureRandom;
import org.checkerframework.checker.linear.qual.Unique;

class EnsureUniqueTest {
    // For example, suppose state0 is a initial state and will be changed
    // to state1 after being called by test1, while test 2 only accepts
    // state0.
    // One question is should it be only used on method parameter?
    // multi args mean all allowed
    // change the postcondition into @top and try to show some msg.
    // relation between top and mayalias
    public void test1(byte @Unique [] bytes) {
        byte @Unique({}) [] bytesIV = bytes;
        SecureRandom secureRandom = new SecureRandom();
        // After this method call,  byte @Unique [] bytesIV became  byte @Unique("initialized") []
        // bytesIV
        secureRandom.nextBytes(bytesIV);
    }
}
