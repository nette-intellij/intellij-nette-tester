package cz.jiripudil.intellij.nette.tester.configuration.editor;

import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.ui.components.JBLabel;
import com.jetbrains.php.run.PhpCommandLineSettings;
import cz.jiripudil.intellij.nette.tester.TesterBundle;
import cz.jiripudil.intellij.nette.tester.configuration.TesterRunConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PhpCommandLineSettingsEditor extends SettingsEditor<TesterRunConfiguration> {
    @NotNull final private Project project;

    private JPanel panel;
    private RawCommandLineEditor interpreterOptions;
    private TextFieldWithBrowseButton customWorkingDirectory;
    private EnvironmentVariablesComponent environmentVariables;

    private JBLabel interpreterOptionsLabel;
    private JBLabel workingDirectoryLabel;

    PhpCommandLineSettingsEditor(@NotNull final Project project) {
        this.project = project;
    }

    private void createUIComponents() {
        interpreterOptions = new RawCommandLineEditor();

        customWorkingDirectory = new TextFieldWithBrowseButton();
        customWorkingDirectory.addBrowseFolderListener(null, null, project, FileChooserDescriptorFactory.createSingleFolderDescriptor());

        environmentVariables = new EnvironmentVariablesComponent();

        interpreterOptionsLabel = new JBLabel(TesterBundle.message("runConfiguration.editor.cli.interpreterOptions"));
        workingDirectoryLabel = new JBLabel(TesterBundle.message("runConfiguration.editor.cli.workingDirectory"));
    }

    @Override
    protected void resetEditorFrom(@NotNull TesterRunConfiguration runConfiguration) {
        PhpCommandLineSettings commandLineSettings = runConfiguration.getSettings().getPhpCommandLineSettings();
        interpreterOptions.setText(commandLineSettings.getParameters());
        customWorkingDirectory.setText(commandLineSettings.getWorkingDirectory());
        environmentVariables.setEnvs(commandLineSettings.getEnvs());
        environmentVariables.setPassParentEnvs(commandLineSettings.isPassParentEnvs());
    }

    @Override
    protected void applyEditorTo(@NotNull TesterRunConfiguration runConfiguration) throws ConfigurationException {
        PhpCommandLineSettings commandLineSettings = runConfiguration.getSettings().getPhpCommandLineSettings();
        commandLineSettings.setParameters(interpreterOptions.getText());
        commandLineSettings.setWorkingDirectory(customWorkingDirectory.getText());
        commandLineSettings.setEnvs(environmentVariables.getEnvs());
        commandLineSettings.setPassParentEnvs(environmentVariables.isPassParentEnvs());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return panel;
    }
}
