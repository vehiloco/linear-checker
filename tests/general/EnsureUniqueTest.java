package general;

import org.checkerframework.checker.linear.qual.Unique;

import java.security.SecureRandom;

import javax.crypto.spec.IvParameterSpec;

class EnsureUniqueTest {
    // For example, suppose state0 is a initial state and will be changed
    // to state1 after being called by test1, while test 2 only accepts
    // state0.
    // One question is should it be only used on method parameter?
    // multi args mean all allowed
    // change the postcondition into @top and try to show some msg.
    // relation between top and mayalias
    /*
    states = {"", "initialized", "used"}
    * */
    public void test1(byte @Unique [] bytes) {
        // New objects shoule be unique.
        byte @Unique({}) [] bytesIV = new byte @Unique({}) [16];
        SecureRandom secureRandom = new SecureRandom();
        // After this method call,  byte @Unique [] bytesIV becomes  byte @Unique("initialized")[]
        secureRandom.nextBytes(bytesIV);
        byte @Unique({}) [] newBytesIv;
        // transfer state and the rhs becomes disappear
        newBytesIv = bytesIV;
        // newBytesIv becomes @Unique({"used"}).
        // TODO: look into why there is an new error here
        // :: error: (argument.type.incompatible)
        IvParameterSpec ivSpec = new IvParameterSpec(newBytesIv);
        byte @Unique({}) [] testBytesIv;
        testBytesIv = newBytesIv;
        // :: error: (argument.type.incompatible)
        IvParameterSpec ivSpec2 = new IvParameterSpec(testBytesIv);
    }
}
