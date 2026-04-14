/*
 * creedengo - Python language - Provides rules to reduce the environmental footprint of your Python programs
 * Copyright © 2024 Green Code Initiative (https://green-code-initiative.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.greencodeinitiative.creedengo.python.integration.tests;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.greencodeinitiative.creedengo.integration.tests.GCIRulesBase;
import org.junit.jupiter.api.Test;
import org.sonarqube.ws.Issues;
import org.sonarqube.ws.Measures;

class GCIRulesIT extends GCIRulesBase {

    @Test
    void testMeasuresAndIssues() {
        String projectKey = analyzedProjects.get(0).getProjectKey();

        Map<String, Measures.Measure> measures = getMeasures(projectKey);

        assertThat(ofNullable(measures.get("code_smells")).map(Measures.Measure::getValue).map(Integer::parseInt).orElse(0))
                .isGreaterThan(1);

        List<Issues.Issue> projectIssues = searchIssuesForComponent(projectKey, null).getIssuesList();
        assertThat(projectIssues).isNotEmpty();

    }

    @Test
    void testGCI74() {

        String filePath = "src/avoidFullSQLRequest.py";
        String ruleId = "creedengo-python:GCI74";
        String ruleMsg = "Don't use the query SELECT * FROM";
        int[] startLines = new int[]{4, 7};
        int[] endLines = new int[]{4, 7};

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_20MIN);

    }

    @Test
    void testGCI24() {

        String filePath = "src/avoidUnlimitedSQLRequest.py";
        String ruleId = "creedengo-python:GCI24";
        String ruleMsg = "Don't use the query SELECT _ FROM _ WHERE _ without a limit";
        int[] startLines = new int[]{4, 7};
        int[] endLines = new int[]{4, 7};

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY_MAJOR, TYPE, EFFORT_15MIN);

    }

    @Test
    void testGCI7_compliant() {

        String filePath = "src/avoidGettersAndSettersCompliant.py";
        String ruleId = "creedengo-python:GCI7";
        String ruleMsg = "Avoid creating getter and setter methods in classes";
        int[] startLines = new int[]{};
        int[] endLines = new int[]{};

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_5MIN);

    }

    @Test
    void testGCI7_nonCompliant() {

        String filePath = "src/avoidGettersAndSettersNonCompliant.py";
        String ruleId = "creedengo-python:GCI7";
        String ruleMsg = "Avoid creating getter and setter methods in classes";
        int[] startLines = new int[]{9, 12, 19, 25};
        int[] endLines = new int[]{9, 12, 19, 25};

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_5MIN);

    }

    @Test
    void testGCI4_compliant() {

        String filePath = "src/avoidGlobalVariableInFunctionCompliant.py";
        String ruleId = "creedengo-python:GCI4";
        String ruleMsg = "Use local variable (function/class scope) instead of global variable (application scope)";
        int[] startLines = new int[]{};
        int[] endLines = new int[]{};

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_5MIN);

    }

    @Test
    void testGCI4_nonCompliant() {

        String filePath = "src/avoidGlobalVariableInFunctionNonCompliant.py";
        String ruleId = "creedengo-python:GCI4";
        String ruleMsg = "Use local variable (function/class scope) instead of global variable (application scope)";
        int[] startLines = new int[]{4, 5, 6, 7, 9, 11};
        int[] endLines = new int[]{4, 5, 6, 7, 9, 11};

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_5MIN);

    }

    @Test
    void testGCI404() {

        String filePath = "src/avoidListComprehensionInIterations.py";
        String ruleId = "creedengo-python:GCI404";
        String ruleMsg = "Use generator comprehension instead of list comprehension in for loop declaration";
        int[] startLines = new int[]{2, 6, 10};
        int[] endLines = new int[]{2, 6, 10};

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_15MIN);

    }

    @Test
    void testGCI2_compliant() {

        String filePath = "src/avoidMultipleIfElseStatementCompliant.py";
        String ruleId = "creedengo-python:GCI2";
        String ruleMsg = "Use a match-case statement instead of multiple if-else if possible";
        int[] startLines = new int[]{};
        int[] endLines = new int[]{};

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_5MIN);

    }

    @Test
    void testGCI2_nonCompliant() {

        String filePath = "src/avoidMultipleIfElseStatementNonCompliant.py";
        String ruleId = "creedengo-python:GCI2";
        String ruleMsg = "Use a match-case statement instead of multiple if-else if possible";
        int[] startLines = new int[]{
                20, 31, 33, 50, 62, 77,
                79, 92, 95, 97, 111, 116,
                135, 148, 150, 151, 153, 168,
                184, 186
        };
        int[] endLines = new int[]{
                20, 31, 33, 50, 62, 77,
                79, 92, 95, 97, 111, 116,
                135, 148, 150, 151, 153, 168,
                184, 186
        };

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_5MIN);

    }

    @Test
    void testGCI72() {

        String filePath = "src/avoidSQLRequestInLoop.py";
        String ruleId = "creedengo-python:GCI72";
        String ruleMsg = "Avoid performing SQL queries within a loop";
        int[] startLines = new int[]{11, 21, 31, 40};
        int[] endLines = new int[]{11, 21, 31, 40};

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_10MIN);

    }

    @Test
    void testGCI72_check() {

        String filePath = "src/avoidSQLRequestInLoopCheck.py";
        String ruleId = "creedengo-python:GCI72";
        String ruleMsg = "Avoid performing SQL queries within a loop";
        int[] startLines = new int[]{27, 44, 62};
        int[] endLines = new int[]{27, 44, 62};

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_10MIN);

    }

    @Test
    void testGCI72_noImports() {

        String filePath = "src/avoidSQLRequestInLoopNoImports.py";
        String ruleId = "creedengo-python:GCI72";
        String ruleMsg = "Avoid performing SQL queries within a loop";
        int[] startLines = new int[]{};
        int[] endLines = new int[]{};

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_10MIN);

    }

    @Test
    void testGCI35() {

        String filePath = "src/avoidTryCatchWithFileOpenedCheck.py";
        String ruleId = "creedengo-python:GCI35";
        String ruleMsg = "Avoid the use of try-catch with a file open in try block";
        int[] startLines = new int[]{17};
        int[] endLines = new int[]{17};

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_5MIN);

    }

    @Test
    void testGCI89_compliant() {

        String filePath = "src/avoidUnlimitedCacheCompliant.py";
        String ruleId = "creedengo-python:GCI89";
        String ruleMsg = "Do not set cache size to unlimited";
        int[] startLines = new int[]{};
        int[] endLines = new int[]{};

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_5MIN);

    }

    @Test
    void testGCI89_nonCompliant() {

        String filePath = "src/avoidUnlimitedCacheNonCompliant.py";
        String ruleId = "creedengo-python:GCI89";
        String ruleMsg = "Do not set cache size to unlimited";
        int[] startLines = new int[]{6, 10, 15, 20};
        int[] endLines = new int[]{6, 10, 15, 20};

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY_MAJOR, TYPE, EFFORT_5MIN);

    }

    @Test
    void testGCI10() {

        String filePath = "src/avoidUnoptimizedVectorImages.py";
        String ruleId = "creedengo-python:GCI10";
        String ruleMsg = "Avoid using unoptimized vector images";
        int[] startLines = new int[]{2, 3, 4, 5};
        int[] endLines = new int[]{2, 3, 4, 5};

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_1H);

    }

    @Test
    void testGCI203() {

        String filePath = "src/detectUnoptimizedImageFormat.py";
        String ruleId = "creedengo-python:GCI203";
        String ruleMsg = "If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.";
        int[] startLines = new int[]{
                8, 9, 10, 11, 12, 13,
                14, 15, 16, 17, 18, 19,
                21, 23, 24, 25, 26, 27,
                28, 29, 30, 31, 32, 33,
                34, 35
        };
        int[] endLines = new int[]{
                8, 9, 10, 11, 12, 13,
                14, 15, 16, 17, 18, 19,
                21, 23, 24, 25, 26, 27,
                28, 29, 30, 31, 32, 33,
                34, 35
        };

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_1H);

    }

    @Test
    void testGCI203_compliant() {

        String filePath = "src/detectUnoptimizedImageFormatCompliant.py";
        String ruleId = "creedengo-python:GCI203";
        String ruleMsg = "If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.";
        int[] startLines = new int[]{};
        int[] endLines = new int[]{};

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_1H);

    }

    @Test
    void testGCI96() {
        String filePath = "src/pandasRequireUsecols.py";
        String ruleId = "creedengo-python:GCI96";
        String ruleMsg = "Specify 'usecols' or 'columns' when reading a DataFrame using Pandas to load only necessary columns";
        int[] startLines = new int[]{
            3, 4, 5, 6, 7, 16, 19
        };
        int[] endLines = new int[]{
            3, 4, 5, 6, 7, 16, 19
        };

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_10MIN);
    }

    @Test
    void testGCI97(){
        String filePath = "src/optimizeSquareComputation.py";
        String ruleId = "creedengo-python:GCI97";
        String ruleMsg = "Use x*x instead of x**2 or math.pow(x,2) to calculate the square of a value";
        int[] startLines = new int[]{
            4, 7, 19, 20, 25, 26, 31, 38
        };
        int[] endLines = new int[]{
            4, 7, 19, 20, 25, 26, 31, 38
        };

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_1MIN);
    }

    @Test
    void testGCI99(){
        String filePath = "src/avoidCSVFormat.py";
        String ruleId = "creedengo-python:GCI99";
        String ruleMsg = "Use Parquet or Feather format instead of CSV";
        int[] startLines = new int[]{
                // FIXME DDC : check why line 17 is not detected TI but detected in UT !!!
//                4, 6, 10, 12, 14, 15, 17, 18, 23, 39, 47, 48
                4, 6, 10, 12, 14, 15, 18, 23, 39, 47, 48
        };
        int[] endLines = new int[]{
//                4, 6, 10, 12, 14, 15, 17, 18, 23, 39, 47, 48
                4, 6, 10, 12, 14, 15, 18, 23, 39, 47, 48
        };

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_50MIN);
    }

    @Test
    void testGCI100() {

        String filePath = "src/disableGradientForModelEval.py";
        String ruleId = "creedengo-python:GCI100";
        String ruleMsg = "PyTorch : Disable gradient computation when evaluating a model to save memory and computation time";
        int[] startLines = new int[]{
            19, 29, 38
        };
        int[] endLines = new int[]{
            19, 29, 38
        };

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_10MIN);
    }

    // FIXME: no issues are detected since last libraries upgrade (12/09/2025)
    @Test
    void testGCI101(){
        String filePath = "src/avoidConvBiasBeforeBatchNorm.py";
        String ruleId = "creedengo-python:GCI101";
        String ruleMsg = "Remove bias for convolutions before batch norm layers to save time and memory.";
        int[] startLines = new int[]{
            49, 71, 115, 136, 156, 178
        };
        int[] endLines = new int[]{
            49, 71, 115, 136, 156, 178
        };

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_10MIN);
    }

    @Test
    void testGCI102(){
        String filePath = "src/avoidNonPinnedMemoryForDataloaders.py";
        String ruleId = "creedengo-python:GCI102";
        String ruleMsg = "Use pinned memory to reduce data transfer in RAM.";
        int[] startLines = new int[]{
                7, 8, 9, 10, 11, 12, 13, 14
        };
        int[] endLines = new int[]{
                7, 8, 9, 10, 11, 12, 13, 14
        };

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_10MIN);
    }

    @Test
    void testGCI103(){

        String filePath = "src/dictionaryItemsUnused.py";
        String ruleId = "creedengo-python:GCI103";
        String ruleMsg = "Use dict.keys() or dict.values() instead of dict.items() when only one part of the key-value pair is used";
        int[] startLines = new int[]{
            5, 8, 24, 27, 36
        };
        int[] endLines = new int[]{
            5, 8, 24, 27, 36
        };

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_1MIN);
    }

    @Test
    void testGCI104() {

        String filePath = "src/avoidCreatingTensorUsingNumpyOrNativePython.py";
        String ruleId = "creedengo-python:GCI104";
        String ruleMsg = "Directly create tensors as torch.Tensor instead of using numpy functions.";
        int[] startLines = new int[]{
            5, 15, 19, 24
        };
        int[] endLines = new int[]{
            5, 15, 19, 24
        };

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_10MIN);
    }

    @Test
    void testGCI105() {

        String filePath = "src/stringConcatenation.py";
        String ruleId = "creedengo-python:GCI105";
        String ruleMsg = "Concatenation of strings should be done using f-strings or str.join()";
        int[] startLines = new int[]{
            5, 8, 10, 32, 38
        };
        int[] endLines = new int[]{
            5, 8, 10, 32, 38
        };

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_1MIN);
    }

    @Test
    void testGCI106() {
        String filePath = "src/avoidSqrtInLoop.py";
        String ruleId = "creedengo-python:GCI106";
        String ruleMsg = "Avoid using scalar sqrt functions in loops. Apply vectorized sqrt operations on arrays directly.";
        int[] startLines = new int[]{
                7, 11, 16, 21, 45, 52, 60
        };
        int[] endLines = new int[]{
                7, 11, 16, 21, 45, 52, 60
        };
        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_5MIN);

    }

    @Test
    void testGCI107(){

        String filePath = "src/avoidIterativeMatrixOperations.py";
        String ruleId = "creedengo-python:GCI107";
        String ruleMsg = "Avoid iterative matrix operations, use numpy dot or outer function instead";
        int[] startLines = new int[]{
            8, 20, 36, 46, 75, 83, 91, 106, 115
        };
        int[] endLines = new int[]{
            8, 20, 36, 46, 75, 83, 91, 106, 115
        };

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_10MIN);
    }

    @Test
    void testGCI108(){
        String filePath = "src/preferAppendLeft.py";
        String ruleId = "creedengo-python:GCI108";
        String ruleMsg = "Use appendleft with deque instead of .insert(0, val) for modification at the beginning of a list";
        int[] startLines = new int[]{
            5, 8, 11, 14, 17, 20, 23, 25, 31, 35, 42
        };
        int[] endLines = new int[]{
            5, 8, 11, 14, 17, 20, 23, 25, 31, 35, 42
        };

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_10MIN);
    }

    @Test
    void testGCI109() {
        String filePath = "src/avoidExceptionsForControlFlow.py";
        String ruleId = "creedengo-python:GCI109";
        String ruleMsg = "Avoid using exceptions for control flow";
        int[] startLines = new int[]{
                4, 10, 16, 22, 29, 35, 41, 47
        };
        int[] endLines = new int[]{
                4, 10, 16, 22, 29, 35, 41, 47
        };

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY_INFO, TYPE, EFFORT_10MIN);
    }

    @Test
    void testGCI110(){
        String filePath = "src/avoidWildcardImports.py";
        String ruleId = "creedengo-python:GCI110";
        String ruleMsg = "Avoid wildcard imports";
        int[] startLines = new int[]{
            2, 3, 4, 5, 7, 9, 10, 12
        };
        int[] endLines = new int[]{
            2, 3, 4, 5, 7, 9, 10, 12
        };

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_2MIN);
    }

    @Test
    void testGCI111_logging_nonCompliant() {
        String filePath = "src/detectBadLoggingFormatInterpolationLoggingNonCompliant.py";
        String ruleId = "creedengo-python:GCI111";
        String ruleMsg = "For logging format, prefer using %s with kwargs instead of builtin formatter \"\".format() or f\"\"";
        int[] startLines = new int[]{
            9, 10, 11, 12, 13, 14, 15, 16, 17,      // logging direct methods
            21, 22, 23, 24, 25, 26, 27,             // logger via getLogger
            31,                                     // log via getLogger imported
            35,                                     // LOGGER via Logger class
            38, 39                                  // f-strings
        };
        int[] endLines = new int[]{
            9, 10, 11, 12, 13, 14, 15, 16, 17,      // logging direct methods
            21, 22, 23, 24, 25, 26, 27,             // logger via getLogger
            31,                                     // log via getLogger imported
            35,                                     // LOGGER via Logger class
            38, 39                                  // f-strings
        };

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_5MIN);
    }

    @Test
    void testGCI111_logging_compliant() {
        String filePath = "src/detectBadLoggingFormatInterpolationLoggingCompliant.py";
        String ruleId = "creedengo-python:GCI111";
        String ruleMsg = "For logging format, prefer using %s with kwargs instead of builtin formatter \"\".format() or f\"\"";
        int[] startLines = new int[]{};
        int[] endLines = new int[]{};

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_5MIN);
    }

    @Test
    void testGCI111_loguru_nonCompliant() {
        String filePath = "src/detectBadLoggingFormatInterpolationLoguruNonCompliant.py";
        String ruleId = "creedengo-python:GCI111";
        String ruleMsg = "For logging format, prefer using %s with kwargs instead of builtin formatter \"\".format() or f\"\"";
        int[] startLines = new int[]{
            9, 10, 11, 12, 13, 14, 15,              // loguru methods with .format()
            18, 19                                  // loguru with f-strings
        };
        int[] endLines = new int[]{
            9, 10, 11, 12, 13, 14, 15,              // loguru methods with .format()
            18, 19                                  // loguru with f-strings
        };

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_5MIN);
    }

    @Test
    void testGCI111_loguru_compliant() {
        String filePath = "src/detectBadLoggingFormatInterpolationLoguruCompliant.py";
        String ruleId = "creedengo-python:GCI111";
        String ruleMsg = "For logging format, prefer using %s with kwargs instead of builtin formatter \"\".format() or f\"\"";
        int[] startLines = new int[]{};
        int[] endLines = new int[]{};

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_5MIN);
    }

    @Test
    void testGCI112_compliant() {

        String filePath = "src/usingSlotsOnDataClassesCompliant.py";
        String ruleId = "creedengo-python:GCI112";
        String ruleMsg = "From python >= 3.10, reduce memory footprint by using @dataclass(slots=True)";
        int[] startLines = new int[]{};
        int[] endLines = new int[]{};

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_1MIN);

    }

    @Test
    void testGCI112_nonCompliant() {

        String filePath = "src/usingSlotsOnDataClassesNonCompliant.py";
        String ruleId = "creedengo-python:GCI112";
        String ruleMsg = "From python >= 3.10, reduce memory footprint by using @dataclass(slots=True)";
        int[] startLines = new int[]{1, 7, 13, 19};
        int[] endLines = new int[]{1, 7, 13, 19};

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_1MIN);

    }

}
