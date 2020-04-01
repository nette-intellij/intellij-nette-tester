package cz.jiripudil.intellij.nette.tester.execution;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.text.StringUtil;
import com.jetbrains.php.config.commandLine.PhpCommandSettings;
import com.jetbrains.php.config.interpreters.PhpConfigurationOptionData;
import com.jetbrains.php.config.interpreters.PhpInterpreter;
import com.jetbrains.php.run.filters.XdebugCallStackFilter;
import cz.jiripudil.intellij.nette.tester.TesterUtil;
import cz.jiripudil.intellij.nette.tester.configuration.TesterRunConfiguration;
import cz.jiripudil.intellij.nette.tester.configuration.TesterSettings;
import cz.jiripudil.intellij.nette.tester.projectSettings.TesterProjectSettings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

public class TesterExecutionUtil {
    public static void addCommandArguments(@NotNull Project project, @NotNull PhpCommandSettings command, TesterSettings settings, List<String> arguments) throws ExecutionException {
        PhpInterpreter testEnvironmentInterpreter = settings.getPhpInterpreter(project);
        if (testEnvironmentInterpreter != null && testEnvironmentInterpreter.getPathToPhpExecutable() != null) {
            command.addArgument("-p");
            command.addArgument(testEnvironmentInterpreter.getPathToPhpExecutable());
        }

        if (settings.getUseSystemPhpIni()) {
            command.addArgument("-C");

        } else if (!StringUtil.isEmpty(settings.getPhpIniPath())) {
            command.addArgument("-c");
            command.addArgument(settings.getPhpIniPath());
        }

        try {
            Path tempDir = getTempPath(project);
            if (!Files.isDirectory(tempDir)) {
                Files.createDirectory(tempDir);
            }

            TesterProjectSettings testerSettings = TesterUtil.getTesterSettings(project);
            String setupFile = testerSettings.getSetupFile();

            Path setupScriptPath = Paths.get(tempDir.toString(), setupFile);
            InputStream setupResourceStream = TesterExecutionUtil.class.getClassLoader().getResourceAsStream(setupFile);
            if (setupResourceStream == null) {
                throw new ExecutionException("Input stream can not be null");
            }
            Files.copy(setupResourceStream, setupScriptPath, StandardCopyOption.REPLACE_EXISTING);
            setupResourceStream.close();

            command.addArgument("--setup");
            command.addArgument(command.getPathProcessor().process(setupScriptPath.toString()));

        } catch (IOException e) {
            throw new ExecutionException(e);
        }

        if (!StringUtil.isEmpty(settings.getTesterOptions())) {
            String[] optionsArray = settings.getTesterOptions().split(" ");
            command.addArguments(Arrays.asList(optionsArray));
        }

        for (PhpConfigurationOptionData configurationOption : settings.getPhpInterpreterOptions()) {
            command.addArgument("-d");
            command.addArgument(configurationOption.getName() + (!configurationOption.getValue().isEmpty() ? "=" + configurationOption.getValue() : ""));
        }

        command.addArguments(arguments);
        command.addArgument(settings.getTestScope());
    }

    private static Path getTempPath(Project project) {
        if (project.getBasePath() != null) {
            return Paths.get(project.getBasePath(), ".idea", "intellij-nette-tester");
        } else {
            return Paths.get(PathManager.getPluginsPath(), "intellij-nette-tester");
        }
    }

    public static ConsoleView createConsole(Project project, ProcessHandler processHandler, ExecutionEnvironment executionEnvironment, TesterTestLocator locationProvider) {
        TesterRunConfiguration profile = (TesterRunConfiguration) executionEnvironment.getRunProfile();

        TesterConsoleProperties properties = new TesterConsoleProperties(profile, executionEnvironment.getExecutor(), locationProvider);
        properties.addStackTraceFilter(new XdebugCallStackFilter(project, locationProvider.getPathMapper()));

        BaseTestsOutputConsoleView testsOutputConsoleView = SMTestRunnerConnectionUtil.createConsole("Nette Tester", properties);
        testsOutputConsoleView.addMessageFilter(new TesterStackTraceFilter(project, locationProvider.getPathMapper()));
        testsOutputConsoleView.attachToProcess(processHandler);
        Disposer.register(project, testsOutputConsoleView);
        return testsOutputConsoleView;
    }
}
