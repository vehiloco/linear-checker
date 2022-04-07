// package general;
//
// import org.checkerframework.checker.crypto.qual.RequireUnique;
// import org.checkerframework.checker.crypto.qual.Unique;
//
// class RequireUniqueTest {
//    @RequireUnique(value = "#1", whatever = "algo1")
//    void test1(@Unique({"algo1"}) String x, @Unique({"algo1"}) String y) {}
//
//    @RequireUnique(value = "#1", whatever = "algo2")
//    private static void test2(@Unique({"algo1"}) String x, @Unique({"algo1"}) String y) {
//        x = y;
//    }
//
//    @Unique({"algo3"})
//    String x;
//
//    @Unique({"algo1"})
//    String y;
//    test2(x, y);
// }
