package tests;

import java.io.File;
import java.util.List;
import org.checkerframework.checker.linear.LinearChecker;
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

public class GeneralTest extends CheckerFrameworkPerDirectoryTest {
    public GeneralTest(List<File> testFiles) {
        super(testFiles, LinearChecker.class, "general", "-Anomsgtext", "-Astubs=./");
    }

    @Parameters
    public static String[] getTestDirs() {
        return new String[] {"general"};
    }
}
