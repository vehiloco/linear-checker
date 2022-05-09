package tests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.linear.LinearChecker;
import org.checkerframework.framework.test.CheckerFrameworkPerFileTest;
import org.checkerframework.framework.test.TestUtilities;
import org.junit.runners.Parameterized.Parameters;

public class GeneralTest extends CheckerFrameworkPerFileTest {
    public GeneralTest(File testFile) {
        super(
                testFile,
                LinearChecker.class,
                "general",
                "-Anomsgtext",
                "-Astubs=security.astub",
                "-AnonNullStringsConcatenation",
                "-nowarn");
    }

    @Parameters
    public static List<File> getTestFiles() {
        return new ArrayList<>(TestUtilities.findRelativeNestedJavaFiles("tests", "general"));
    }
}
