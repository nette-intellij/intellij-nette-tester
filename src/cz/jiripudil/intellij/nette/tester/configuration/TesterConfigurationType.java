package cz.jiripudil.intellij.nette.tester.configuration;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.jetbrains.php.PhpIcons;

public class TesterConfigurationType extends ConfigurationTypeBase {
    protected TesterConfigurationType() {
        super("nette-tester", "Nette Tester", "Run tests via Nette Tester", PhpIcons.PHP_TEST_FILE);
        this.addFactory(new TesterConfigurationFactory(this));
    }

    static TesterConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(TesterConfigurationType.class);
    }
}
