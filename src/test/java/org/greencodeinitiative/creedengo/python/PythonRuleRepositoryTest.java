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
package org.greencodeinitiative.creedengo.python;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.sonar.api.SonarRuntime;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.Version;
import org.sonar.check.Rule;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class PythonRuleRepositoryTest {

    private PythonRuleRepository rulesDefinition;
    private RulesDefinition.Repository repository;

    @BeforeEach
    void init() {
        final SonarRuntime sonarRuntime = mock(SonarRuntime.class);
        doReturn(Version.create(0, 0)).when(sonarRuntime).getApiVersion();
        rulesDefinition = new PythonRuleRepository(sonarRuntime);
        RulesDefinition.Context context = new RulesDefinition.Context();
        rulesDefinition.define(context);
        repository = context.repository(rulesDefinition.repositoryKey());
    }

    @Test
    @DisplayName("Test repository metadata")
    void testMetadata() {
        assertThat(repository.name()).isEqualTo("creedengo");
        assertThat(repository.language()).isEqualTo("py");
        assertThat(repository.key()).isEqualTo("creedengo-python");
    }

    @Test
    void testRegistredRules() {
        assertThat(rulesDefinition.checkClasses())
                .describedAs("All implemented rules must be registered into " + PythonRuleRepository.class)
                .containsExactlyInAnyOrder(getDefinedRules().toArray(new Class[0]));
    }

    @Test
    void checkNumberRules() {
        assertThat(repository.rules()).hasSize(PythonRuleRepository.ANNOTATED_RULE_CLASSES.size());
    }

    @Test
    @DisplayName("All rule keys must be prefixed by 'GCI'")
    void testRuleKeyPrefix() {
        SoftAssertions assertions = new SoftAssertions();
        repository.rules().forEach(
                rule -> assertions.assertThat(rule.key()).startsWith("GCI")
        );
        assertions.assertAll();
    }

    @Test
    void testAllRuleParametersHaveDescription() {
        SoftAssertions assertions = new SoftAssertions();
        repository.rules().stream()
                .flatMap(rule -> rule.params().stream())
                .forEach(param -> assertions.assertThat(param.description())
                        .as("description for " + param.key())
                        .isNotEmpty());
        assertions.assertAll();
    }

    private static Set<Class<?>> getDefinedRules() {
        Reflections r = new Reflections(PythonRuleRepository.class.getPackageName() + ".checks");
        return r.getTypesAnnotatedWith(Rule.class);
    }
}
