package cz.jiripudil.intellij.nette.tester.execution;

import com.intellij.execution.Executor;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.execution.testframework.sm.runner.SMTestLocator;
import cz.jiripudil.intellij.nette.tester.configuration.TesterRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TesterConsoleProperties extends SMTRunnerConsoleProperties {
    private final SMTestLocator locator;

    TesterConsoleProperties(@NotNull TesterRunConfiguration config, Executor executor, SMTestLocator locator) {
        super(config, "Nette Tester", executor);
        this.locator = locator;
    }

    @Nullable
    @Override
    public SMTestLocator getTestLocator() {
        return locator;
    }
}
