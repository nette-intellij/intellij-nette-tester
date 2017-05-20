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
import com.intellij.openapi.util.text.StringUtil;
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
        return new TesterRunConfigurationEditor(getProject());
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
            VirtualFile scopeDirectory = PhpRunUtil.findDirectory(settings.getTestScope());
            VirtualFile scopeFile = PhpRunUtil.findFile(settings.getTestScope());
            if (StringUtil.isEmpty(settings.getTestScope())) {
                throw new RuntimeConfigurationError("You must specify the test scope.");

            } else if (scopeDirectory == null && scopeFile == null) {
                throw new RuntimeConfigurationError("File or directory set as the test scope was not found.");
            }

            PhpInterpreter phpInterpreter = settings.getPhpInterpreter(getProject());
            if (phpInterpreter == null) {
                throw new RuntimeConfigurationError("Test environment PHP interpreter not found.");

            } else if (phpInterpreter.getPathToPhpExecutable() == null) {
                throw new RuntimeConfigurationError("Test environment PHP executable not found.");
            }

            if (StringUtil.isEmpty(settings.getTesterExecutable())) {
                throw new RuntimeConfigurationError("You must specify path to Tester executable.");

            } else if (PhpRunUtil.findFile(settings.getTesterExecutable()) == null) {
                throw new RuntimeConfigurationError("Tester executable was not found at given path.");
            }

            if (!settings.getUseSystemPhpIni() && StringUtil.isNotEmpty(settings.getPhpIniPath()) && PhpRunUtil.findFile(settings.getPhpIniPath()) == null) {
                throw new RuntimeConfigurationError("The php.ini file was not found at given path.");
            }

            if (StringUtil.isNotEmpty(settings.getSetupScriptPath()) && PhpRunUtil.findFile(settings.getSetupScriptPath()) == null) {
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
            command.setScript(getSettings().getTesterExecutable(), false);

            command.importCommandLineSettings(getSettings().getPhpCommandLineSettings(), null);
            command.addEnvs(envParameters);

            // support for user setup
            if (getSettings().getSetupScriptPath() != null) {
                command.addEnv("INTELLIJ_NETTE_TESTER_USER_SETUP", getSettings().getSetupScriptPath());
            }

            TesterExecutionUtil.addCommandArguments(getProject(), command, getSettings(), arguments);

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
        settings.setTesterExecutable(PhpConfigurationUtil.deserializePath(settings.getTesterExecutable()));
        settings.setTestScope(PhpConfigurationUtil.deserializePath(settings.getTestScope()));
        settings.setPhpIniPath(PhpConfigurationUtil.deserializePath(settings.getPhpIniPath()));
        settings.setSetupScriptPath(PhpConfigurationUtil.deserializePath(settings.getSetupScriptPath()));
    }

    @Override
    protected void fixSettingsBeforeSerialization(@NotNull TesterSettings settings) {
        settings.setTesterExecutable(PhpConfigurationUtil.serializePath(settings.getTesterExecutable()));
        settings.setTestScope(PhpConfigurationUtil.serializePath(settings.getTestScope()));
        settings.setPhpIniPath(PhpConfigurationUtil.serializePath(settings.getPhpIniPath()));
        settings.setSetupScriptPath(PhpConfigurationUtil.serializePath(settings.getSetupScriptPath()));
    }
}
