package cz.jiripudil.intellij.nette.tester.execution;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.jetbrains.php.config.commandLine.PhpCommandSettings;
import com.jetbrains.php.config.interpreters.PhpInterpreter;
import com.jetbrains.php.run.PhpRunConfiguration;
import com.jetbrains.php.run.filters.XdebugCallStackFilter;
import cz.jiripudil.intellij.nette.tester.configuration.settings.TesterSettings;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class TesterExecutionUtil {
    public static void addCommandArguments(@NotNull PhpCommandSettings command, @NotNull PhpInterpreter interpreter, TesterSettings settings, List<String> arguments) {
        command.addArgument("-p");
        command.addArgument(interpreter.getPathToPhpExecutable());

        if (settings.phpIniPath != null && !settings.phpIniPath.isEmpty()) {
            command.addArgument("-c");
            command.addArgument(settings.phpIniPath);
        }

        command.addArgument("-o");
        command.addArgument("teamcity");

        if (settings.testerOptions != null && !settings.testerOptions.isEmpty()) {
            String[] optionsArray = settings.testerOptions.split(" ");
            command.addArguments(Arrays.asList(optionsArray));
        }

        command.addArguments(arguments);
        command.addArgument(settings.testScope);
    }

    public static ConsoleView createConsole(Project project, ProcessHandler processHandler, ExecutionEnvironment executionEnvironment, TesterTestLocator locationProvider) {
        PhpRunConfiguration profile = (PhpRunConfiguration) executionEnvironment.getRunProfile();

        TesterConsoleProperties properties = new TesterConsoleProperties(profile, executionEnvironment.getExecutor(), locationProvider);
        properties.addStackTraceFilter(new XdebugCallStackFilter(project, locationProvider.getPathMapper()));

        BaseTestsOutputConsoleView testsOutputConsoleView = SMTestRunnerConnectionUtil.createConsole("Nette Tester", properties);
        testsOutputConsoleView.attachToProcess(processHandler);
        Disposer.register(project, testsOutputConsoleView);
        return testsOutputConsoleView;
    }
}
