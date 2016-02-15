package cz.jiripudil.intellij.nette.tester.configuration.editor;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import cz.jiripudil.intellij.nette.tester.configuration.TesterRunConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class TesterRunConfigurationEditor extends SettingsEditor<TesterRunConfiguration> {
    private JPanel panel;
    private TesterSettingsEditor testerSettingsEditor;
    private PhpCommandLineSettingsEditor phpCommandLineSettingsEditor;

    public void init(Project project) {
        testerSettingsEditor.init(project);
        phpCommandLineSettingsEditor.init(project);
    }

    @Override
    protected void resetEditorFrom(TesterRunConfiguration runConfiguration) {
        testerSettingsEditor.resetEditorFrom(runConfiguration);
        phpCommandLineSettingsEditor.resetEditorFrom(runConfiguration);
    }

    @Override
    protected void applyEditorTo(TesterRunConfiguration runConfiguration) throws ConfigurationException {
        testerSettingsEditor.applyEditorTo(runConfiguration);
        phpCommandLineSettingsEditor.applyEditorTo(runConfiguration);
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return panel;
    }
}
