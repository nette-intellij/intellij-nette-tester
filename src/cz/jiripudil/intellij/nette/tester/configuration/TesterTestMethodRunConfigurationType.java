package cz.jiripudil.intellij.nette.tester.configuration;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.run.PhpRunConfigurationFactoryBase;
import cz.jiripudil.intellij.nette.tester.TesterBundle;
import org.jetbrains.annotations.NotNull;

public class TesterTestMethodRunConfigurationType extends ConfigurationTypeBase {
    protected TesterTestMethodRunConfigurationType() {
        super("nette-tester-method", TesterBundle.message("configurationType.method.displayName"), TesterBundle.message("configurationType.method.description"), PhpIcons.PHP_TEST_METHOD);
        this.addFactory(new PhpRunConfigurationFactoryBase(this) {
            @NotNull
            @Override
            public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new TesterTestMethodRunConfiguration(project, this, "");
            }
        });
    }

    static TesterTestMethodRunConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(TesterTestMethodRunConfigurationType.class);
    }
}
