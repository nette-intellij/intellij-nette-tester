package cz.jiripudil.intellij.nette.tester.execution;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.LocatableConfiguration;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.execution.testframework.sm.runner.SMTestLocator;
import com.jetbrains.php.run.PhpRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TesterConsoleProperties<T extends PhpRunConfiguration & LocatableConfiguration> extends SMTRunnerConsoleProperties {
    private final SMTestLocator locator;

    public TesterConsoleProperties(@NotNull T config, Executor executor, SMTestLocator locator) {
        super(config, "Nette Tester", executor);
        this.locator = locator;
    }

    @Nullable
    @Override
    public SMTestLocator getTestLocator() {
        return locator;
    }
}
