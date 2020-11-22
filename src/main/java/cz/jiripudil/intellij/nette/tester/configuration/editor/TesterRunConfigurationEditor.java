package cz.jiripudil.intellij.nette.tester.configuration.editor;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import cz.jiripudil.intellij.nette.tester.TesterBundle;
import cz.jiripudil.intellij.nette.tester.configuration.TesterRunConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class TesterRunConfigurationEditor extends SettingsEditor<TesterRunConfiguration> {
    @NotNull private final Project project;

    private JPanel panel;

    private JPanel testerSettingsPanel;
    private TesterSettingsEditor testerSettingsEditor;

    private JPanel testEnvironmentSettingsPanel;
    private TesterTestEnvironmentSettingsEditor testEnvironmentSettingsEditor;

    private JPanel cliSettingsPanel;
    private PhpCommandLineSettingsEditor phpCommandLineSettingsEditor;

    public TesterRunConfigurationEditor(@NotNull final Project project) {
        super();
        this.project = project;
    }

    @Override
    protected void resetEditorFrom(@NotNull TesterRunConfiguration runConfiguration) {
        testerSettingsEditor.resetEditorFrom(runConfiguration);
        testEnvironmentSettingsEditor.resetEditorFrom(runConfiguration);
        phpCommandLineSettingsEditor.resetEditorFrom(runConfiguration);
    }

    @Override
    protected void applyEditorTo(@NotNull TesterRunConfiguration runConfiguration) throws ConfigurationException {
        testerSettingsEditor.applyEditorTo(runConfiguration);
        testEnvironmentSettingsEditor.applyEditorTo(runConfiguration);
        phpCommandLineSettingsEditor.applyEditorTo(runConfiguration);
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return panel;
    }

    private void createUIComponents() {
        testerSettingsPanel = new JPanel();
        testerSettingsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, JBColor.LIGHT_GRAY), TesterBundle.message("runConfiguration.editor.tester.title")));
        testerSettingsEditor = new TesterSettingsEditor(project);

        testEnvironmentSettingsPanel = new JPanel();
        testEnvironmentSettingsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, JBColor.LIGHT_GRAY), TesterBundle.message("runConfiguration.editor.testEnv.title")));
        testEnvironmentSettingsEditor = new TesterTestEnvironmentSettingsEditor(project);

        cliSettingsPanel = new JPanel();
        cliSettingsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, JBColor.LIGHT_GRAY), TesterBundle.message("runConfiguration.editor.cli.title")));
        phpCommandLineSettingsEditor = new PhpCommandLineSettingsEditor(project);
    }
}
