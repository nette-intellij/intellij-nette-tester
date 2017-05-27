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
    TesterTestMethodRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @Override
    public Icon getIcon() {
        return PhpIcons.PHP_TEST_METHOD;
    }

    @Override
    public String suggestedName() {
        String method = parseMethod();
        return super.suggestedName() + (method != null ? " " + parseMethod() + "()" : "");
    }

    void setMethod(@NotNull Method method) {
        getSettings().setScriptParameters(method.getName());
    }

    boolean isMethod(@NotNull Method method) {
        return method.getName().equals(parseMethod());
    }

    @Nullable
    private String parseMethod() {
        return getSettings().getScriptParameters();
    }
}
