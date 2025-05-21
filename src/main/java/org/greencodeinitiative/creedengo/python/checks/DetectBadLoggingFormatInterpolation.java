package org.greencodeinitiative.creedengo.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;
import java.util.regex.Pattern;

@Rule(key = "GCI1066")
public class DetectBadLoggingFormatInterpolation extends PythonSubscriptionCheck {

    protected static final String MESSAGE_RULE = "For logging format, prefer using %s with kwargs instead of builtin formatter \"\".format() or f\"\"";
    private static final Pattern PATTERN_FORMAT = Pattern.compile("f['\"].*['\"]");
    private static final Pattern PATTERN_LOGGER = Pattern.compile("logging|logger");
    private static final Pattern PATTERN_LOGGING_METHOD = Pattern.compile("info|debug|error");

    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::visitNodeString);
    }

    public void visitNodeString(SubscriptionContext ctx) {
        CallExpression callExpression = (CallExpression) ctx.syntaxNode();

        QualifiedExpression qualifiedExpression = (QualifiedExpression)((CallExpression)ctx.syntaxNode()).callee();

        if(PATTERN_LOGGING_METHOD.matcher(qualifiedExpression.name().name()).matches()
        && PATTERN_LOGGER.matcher(((Name)qualifiedExpression.qualifier()).name()).matches()
        && PATTERN_FORMAT.matcher(((RegularArgument)callExpression.arguments().get(0)).expression().firstToken().value()).matches()) {
            ctx.addIssue(callExpression, MESSAGE_RULE);
        }
    }

}
