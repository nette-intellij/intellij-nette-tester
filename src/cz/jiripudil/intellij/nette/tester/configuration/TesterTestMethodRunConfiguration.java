package cz.jiripudil.intellij.nette.tester.configuration;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.run.script.PhpScriptRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TesterTestMethodRunConfiguration extends PhpScriptRunConfiguration {
    public TesterTestMethodRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @Override
    public Icon getIcon() {
        return PhpIcons.PHP_TEST_METHOD;
    }

    @Override
    public String suggestedName() {
        return super.suggestedName() + createSuggestedName(parseMethod());
    }

    public static String createSuggestedName(@Nullable String methodName) {
        return methodName != null ? " " + methodName + "()" : "";
    }

    void setMethod(@NotNull Method method) {
        setMethodName(method.getName());
    }

    public void setMethodName(@NotNull String methodName) {
        getSettings().setScriptParameters(methodName);
    }

    boolean isMethod(@NotNull Method method) {
        return method.getName().equals(parseMethod());
    }

    @Nullable
    public String getMethod() {
        return parseMethod();
    }

    @Nullable
    public String parseMethod() {
        return getSettings().getScriptParameters();
    }
}
