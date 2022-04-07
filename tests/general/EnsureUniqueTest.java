package general;

import org.checkerframework.checker.crypto.qual.EnsureUnique;
import org.checkerframework.checker.crypto.qual.Unique;

class EnsureUniqueTest {
    @EnsureUnique(value = "#1", whatever = "algo1")
    void test1(@Unique({"algo1"}) String x, @Unique({"algo1"}) String y) {}

    @EnsureUnique(value = "#1", whatever = "algo2")
    // :: error: contracts.postcondition
    void test2(@Unique({"algo1"}) String x, @Unique({"algo1"}) String y) {}
}
