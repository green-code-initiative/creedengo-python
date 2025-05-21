package org.greencodeinitiative.creedengo.python.checks;

import org.junit.Test;
import org.sonar.python.checks.utils.PythonCheckVerifier;

public class DetectBadLoggingFormatInterpolationTest {

    @Test
    public void test_bad_logging_format_interpolation() {
        PythonCheckVerifier.verify("src/test/resources/checks/detectBadLoggingFormatInterpolationNonCompliant.py", new DetectBadLoggingFormatInterpolation());
        PythonCheckVerifier.verifyNoIssue("src/test/resources/checks/detectBadLoggingFormatInterpolationCompliant.py", new DetectBadLoggingFormatInterpolation());
    }

}
