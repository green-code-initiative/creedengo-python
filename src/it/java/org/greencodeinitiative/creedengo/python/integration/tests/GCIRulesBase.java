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

import org.assertj.core.groups.Tuple;
import org.sonarqube.ws.Common;
import org.sonarqube.ws.Components;
import org.sonarqube.ws.Issues;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarqube.ws.Common.RuleType.CODE_SMELL;
import static org.sonarqube.ws.Common.Severity.MAJOR;
import static org.sonarqube.ws.Common.Severity.MINOR;

class GCIRulesBase extends BuildProjectEngine {

    protected static final String[] EXTRACT_FIELDS = new String[]{
            "rule", "message",
//            "line"
            "textRange.startLine", "textRange.endLine",
//            "textRange.startOffset", "textRange.endOffset",
            "severity", "type",
//            "debt",
            "effort"
    };
    protected static final Common.Severity SEVERITY = MINOR;
    protected static final Common.Severity SEVERITY_MAJOR = MAJOR;
    protected static final Common.RuleType TYPE = CODE_SMELL;
    protected static final String EFFORT_1MIN = "1min";
    protected static final String EFFORT_5MIN = "5min";
    protected static final String EFFORT_10MIN = "10min";
    protected static final String EFFORT_15MIN = "15min";
    protected static final String EFFORT_20MIN = "20min";
    protected static final String EFFORT_50MIN = "50min";
    protected static final String EFFORT_1h = "1h";

    protected void checkIssuesForFile(String filePath, String ruleId, String ruleMsg, int[] startLines, int[] endLines) {
        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_5MIN);
    }

    protected void checkIssuesForFile(String filePath, String ruleId, String ruleMsg, int[] startLines, int[] endLines, Common.Severity severity, Common.RuleType type, String effort) {

        assertThat(startLines.length)
                .isEqualTo(endLines.length);

        String projectKey = analyzedProjects.get(0).getProjectKey();

        String componentKey = projectKey + ":" + filePath;

//        System.out.println("--- COMPONENT KEY : " + componentKey);

        // launch the search
        Components.ShowWsResponse respComponent = showComponent(componentKey);
        Components.Component comp = respComponent.getComponent();
//        System.out.println("--- COMPONENT --- " + comp);
//        System.out.println("--- COMPONENT KEY --- " + comp.getKey());
//        System.out.println("--- COMPONENT PATH --- " + comp.getPath());
//        System.out.println("--- PATH ok --- " + filePath.equals(comp.getPath()));
        assertThat(filePath)
            .withFailMessage("File not found: " + filePath)
            .isEqualTo(comp.getPath());

        // check issues
        Issues.SearchWsResponse respIssues = searchIssuesForComponent(componentKey, ruleId);

//		System.out.println("--- NB ISSUES : " + respIssues.getIssuesCount());
//		System.out.println("--- NB ISSUES_LIST : " + respIssues.getIssuesList().size());
        respIssues.getIssuesList().forEach(issue -> {
            System.out.println("--- Issue --- " + issue.getRule() + " / " + issue.getLine());
		});

//        List<Issues.Issue> issues = issuesForFile(projectKey, filePath, ruleId);
        List<Issues.Issue> issues = respIssues.getIssuesList();

        List<Tuple> expectedTuples = new ArrayList<>();
        for (int i = 0; i < startLines.length; i++) {
            expectedTuples.add(Tuple.tuple(ruleId, ruleMsg, startLines[i], endLines[i], severity, type, effort));
        }

        assertThat(issues)
                .hasSizeGreaterThanOrEqualTo(startLines.length)
//                .hasSize(lines.length)
                .extracting(EXTRACT_FIELDS)
                .containsAll(expectedTuples);
//                .containsExactlyElementsOf(expectedTuples);
    }

}
