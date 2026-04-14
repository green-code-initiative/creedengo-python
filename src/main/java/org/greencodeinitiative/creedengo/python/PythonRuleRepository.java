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

import org.greencodeinitiative.creedengo.python.checks.*;
import org.sonar.api.SonarRuntime;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.python.api.PythonCustomRuleRepository;
import org.sonarsource.analyzer.commons.RuleMetadataLoader;

import java.util.Arrays;
import java.util.List;

public record PythonRuleRepository(SonarRuntime sonarRuntime) implements RulesDefinition, PythonCustomRuleRepository {

    static final List<Class<?>> ANNOTATED_RULE_CLASSES = Arrays.asList(
            AvoidGettersAndSetters.class,
            AvoidGlobalVariableInFunctionCheck.class,
            AvoidSQLRequestInLoop.class,
            AvoidTryCatchWithFileOpenedCheck.class,
            AvoidUnlimitedCache.class,
            AvoidUnlimitedSQLRequest.class,
            AvoidUnoptimizedVectorImagesCheck.class,
            AvoidFullSQLRequest.class,
            AvoidListComprehensionInIterations.class,
            DetectBadLoggingFormatInterpolation.class,
            DetectUnoptimizedImageFormat.class,
            AvoidMultipleIfElseStatementCheck.class,
            PandasRequireUsecolsArgument.class,
            OptimizeSquareComputation.class,
            AvoidSqrtInLoop.class,
            DictionaryItemsUnused.class,
            AvoidCSVFormat.class,
            AvoidIterativeMatrixOperations.class,
            AvoidNonPinnedMemoryForDataloaders.class,
            AvoidConvBiasBeforeBatchNorm.class,
            DisableGradientForModelEval.class,
            StringConcatenation.class,
            PreferAppendLeft.class,
            AvoidCreatingTensorUsingNumpyOrNativePython.class,
            AvoidWildcardImportsCheck.class,
            AvoidExceptionsForControlFlowCheck.class,
            UsingSlotsOnDataClasses.class
    );

    public static final String LANGUAGE = "py";
    public static final String NAME = "creedengo";
    public static final String RESOURCE_BASE_PATH = "org/green-code-initiative/rules/python";
    public static final String REPOSITORY_KEY = "creedengo-python";

    @Override
    public void define(Context context) {
        NewRepository repository = context.createRepository(REPOSITORY_KEY, LANGUAGE).setName(NAME);
        RuleMetadataLoader ruleMetadataLoader = new RuleMetadataLoader(RESOURCE_BASE_PATH, sonarRuntime);
        ruleMetadataLoader.addRulesByAnnotatedClass(repository, checkClasses());
        repository.done();
    }

    @Override
    public String repositoryKey() {
        return REPOSITORY_KEY;
    }

    @Override
    public List<Class<?>> checkClasses() {
        return ANNOTATED_RULE_CLASSES;
    }

}
