package tests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.crypto.CryptoChecker;
import org.checkerframework.framework.test.CheckerFrameworkPerFileTest;
import org.checkerframework.framework.test.TestUtilities;
import org.junit.runners.Parameterized.Parameters;

public class GeneralTest extends CheckerFrameworkPerFileTest {
    public GeneralTest(File testFile) {
        super(
                testFile,
                CryptoChecker.class,
                "general",
                "-Anomsgtext",
                "-Astubs=stubs/",
                "-AnonNullStringsConcatenation",
                "-nowarn");
    }

    @Parameters
    public static List<File> getTestFiles() {
        return new ArrayList<>(TestUtilities.findRelativeNestedJavaFiles("tests", "general"));
    }
}
