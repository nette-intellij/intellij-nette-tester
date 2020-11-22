package cz.jiripudil.intellij.nette.tester.configuration;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.run.PhpRunConfigurationFactoryBase;
import cz.jiripudil.intellij.nette.tester.TesterBundle;
import org.jetbrains.annotations.NotNull;

public class TesterRunConfigurationType extends ConfigurationTypeBase {
    protected TesterRunConfigurationType() {
        super("nette-tester", TesterBundle.message("configurationType.displayName"), TesterBundle.message("configurationType.description"), PhpIcons.PHP_TEST_FILE);
        this.addFactory(createFactory(this));
    }

    static TesterRunConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(TesterRunConfigurationType.class);
    }

    public static ConfigurationFactory createFactory(TesterRunConfigurationType type) {
        return new PhpRunConfigurationFactoryBase(type) {
            @NotNull
            @Override
            public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new TesterRunConfiguration(project, this, this.getName());
            }

            @Override
            public String getName() {
                return TesterBundle.message("configurationType.displayName");
            }
        };
    }
}
