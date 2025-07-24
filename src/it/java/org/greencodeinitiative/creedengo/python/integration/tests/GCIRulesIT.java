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

import org.junit.jupiter.api.Test;
import org.sonarqube.ws.Issues;
import org.sonarqube.ws.Measures;

import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;

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
    void testGCI4() {

        String filePath = "src/avoidGlobalVariableInFunction.py";
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

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_1h);

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

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_1h);

    }

    @Test
    void testGCI203_compliant() {

        String filePath = "src/detectUnoptimizedImageFormatCompliant.py";
        String ruleId = "creedengo-python:GCI203";
        String ruleMsg = "If possible, the utilisation of svg image format (or <svg/> html tag) is recommended over other image format.";
        int[] startLines = new int[]{};
        int[] endLines = new int[]{};

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_1h);

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

        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_1h);
    }

}
