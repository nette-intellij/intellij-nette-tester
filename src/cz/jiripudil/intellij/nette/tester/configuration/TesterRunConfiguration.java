package cz.jiripudil.intellij.nette.tester.configuration;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.php.config.PhpProjectConfigurationFacade;
import com.jetbrains.php.config.commandLine.PhpCommandSettings;
import com.jetbrains.php.config.commandLine.PhpCommandSettingsBuilder;
import com.jetbrains.php.config.interpreters.PhpInterpreter;
import com.jetbrains.php.run.PhpExecutionUtil;
import com.jetbrains.php.run.PhpRunConfiguration;
import com.jetbrains.php.run.PhpRunUtil;
import com.jetbrains.php.util.PhpConfigurationUtil;
import com.jetbrains.php.util.pathmapper.PhpPathMapper;
import cz.jiripudil.intellij.nette.tester.configuration.editor.TesterRunConfigurationEditor;
import cz.jiripudil.intellij.nette.tester.execution.TesterExecutionUtil;
import cz.jiripudil.intellij.nette.tester.execution.TesterTestLocator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TesterRunConfiguration extends PhpRunConfiguration<TesterSettings> {
    TesterRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        TesterRunConfigurationEditor editor = new TesterRunConfigurationEditor();
        editor.init(getProject());

        return editor;
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        PhpInterpreter interpreter = PhpProjectConfigurationFacade.getInstance(getProject()).getInterpreter();
        if (interpreter == null) {
            throw new RuntimeConfigurationError(PhpCommandSettingsBuilder.INTERPRETER_NOT_FOUND_ERROR);

        } else if (interpreter.getPathToPhpExecutable() == null) {
            throw new RuntimeConfigurationError("PHP executable not found.");

        } else {
            TesterSettings settings = getSettings();
            VirtualFile scopeDirectory = PhpRunUtil.findDirectory(settings.testScope);
            VirtualFile scopeFile = PhpRunUtil.findFile(settings.testScope);
            if (settings.testScope == null) {
                throw new RuntimeConfigurationError("You must specify the test scope.");

            } else if (scopeDirectory == null && scopeFile == null) {
                throw new RuntimeConfigurationError("File or directory set as the test scope was not found.");
            }

            if (settings.testerExecutable == null || settings.testerExecutable.isEmpty()) {
                throw new RuntimeConfigurationError("You must specify path to Tester executable.");

            } else if (PhpRunUtil.findFile(settings.testerExecutable) == null) {
                throw new RuntimeConfigurationError("Tester executable was not found at given path.");
            }

            if (settings.phpIniPath != null && !settings.phpIniPath.isEmpty() && PhpRunUtil.findFile(settings.phpIniPath) == null) {
                throw new RuntimeConfigurationError("The php.ini file was not found at given path.");
            }

            if (settings.setupScriptPath != null && !settings.setupScriptPath.isEmpty() && PhpRunUtil.findFile(settings.setupScriptPath) == null) {
                throw new RuntimeConfigurationError("The setup script file was not found at given path.");
            }

            PhpRunUtil.checkCommandLineSettings(getProject(), settings.getPhpCommandLineSettings());
        }
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        return this.getState(executionEnvironment, this.createCommand(Collections.emptyMap(), Collections.emptyList(), false));
    }

    @NotNull
    private RunProfileState getState(@NotNull final ExecutionEnvironment executionEnvironment, @NotNull final PhpCommandSettings command) throws ExecutionException {
        try {
            this.checkConfiguration();
        } catch (RuntimeConfigurationException e) {
            throw new ExecutionException(e.getMessage() + " for " + this.getName() + " run-configuration");
        }

        return new CommandLineState(executionEnvironment) {
            @NotNull
            @Override
            protected ProcessHandler startProcess() throws ExecutionException {
                ProcessHandler processHandler = createProcessHandler(getProject(), command);
                PhpRunUtil.attachProcessOutputDebugDumper(processHandler);
                ProcessTerminatedListener.attach(processHandler, getProject());
                return processHandler;
            }

            @NotNull
            public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
                ProcessHandler processHandler = this.startProcess();

                PhpPathMapper pathMapper = command.getPathProcessor().createPathMapper(getProject());
                TesterTestLocator locationProvider = TesterTestLocator.create(pathMapper);
                ConsoleView consoleView = TesterExecutionUtil.createConsole(getProject(), processHandler, executionEnvironment, locationProvider);
                PhpExecutionUtil.addMessageFilters(getProject(), consoleView, locationProvider.getPathMapper());
                return new DefaultExecutionResult(consoleView, processHandler);
            }
        };
    }

    @NotNull
    private PhpCommandSettings createCommand(Map<String, String> envParameters, List<String> arguments, boolean withDebuggerOptions) throws ExecutionException {
        PhpInterpreter interpreter = PhpProjectConfigurationFacade.getInstance(getProject()).getInterpreter();
        if (interpreter == null) {
            throw new ExecutionException(PhpCommandSettingsBuilder.INTERPRETER_NOT_FOUND_ERROR);

        } else {
            PhpCommandSettings command = PhpCommandSettingsBuilder.create(getProject(), interpreter, withDebuggerOptions);
            command.setScript(getSettings().testerExecutable, false);

            command.importCommandLineSettings(getSettings().getPhpCommandLineSettings(), null);
            command.addEnvs(envParameters);

            // support for user setup
            if (getSettings().setupScriptPath != null) {
                command.addEnv("INTELLIJ_NETTE_TESTER_USER_SETUP", getSettings().setupScriptPath);
            }

            TesterExecutionUtil.addCommandArguments(command, interpreter, getSettings(), arguments);

            return command;
        }
    }

    @NotNull
    @Override
    protected TesterSettings createSettings() {
        return new TesterSettings();
    }

    @Override
    protected void fixSettingsAfterDeserialization(@NotNull TesterSettings settings) {
        settings.testerExecutable = PhpConfigurationUtil.deserializePath(settings.testerExecutable);
        settings.testScope = PhpConfigurationUtil.deserializePath(settings.testScope);
        settings.phpIniPath = PhpConfigurationUtil.deserializePath(settings.phpIniPath);
        settings.setupScriptPath = PhpConfigurationUtil.deserializePath(settings.setupScriptPath);
    }

    @Override
    protected void fixSettingsBeforeSerialization(@NotNull TesterSettings settings) {
        settings.testerExecutable = PhpConfigurationUtil.serializePath(settings.testerExecutable);
        settings.testScope = PhpConfigurationUtil.serializePath(settings.testScope);
        settings.phpIniPath = PhpConfigurationUtil.serializePath(settings.phpIniPath);
        settings.setupScriptPath = PhpConfigurationUtil.serializePath(settings.setupScriptPath);
    }
}
