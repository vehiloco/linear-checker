package tests;

import org.checkerframework.checker.linear.LinearChecker;
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.util.List;

public class GeneralTest extends CheckerFrameworkPerDirectoryTest {
    public GeneralTest(List<File> testFiles) {
        super(
                testFiles,
                LinearChecker.class,
                "general",
                "-Anomsgtext",
                "-Astubs=IvParameterSpec.astub");
    }

    @Parameters
    public static String[] getTestDirs() {
        return new String[] {"general"};
    }
}
