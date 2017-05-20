package cz.jiripudil.intellij.nette.tester.configuration.editor;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.RawCommandLineEditor;
import cz.jiripudil.intellij.nette.tester.configuration.TesterRunConfiguration;
import cz.jiripudil.intellij.nette.tester.configuration.TesterSettings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class TesterSettingsEditor extends SettingsEditor<TesterRunConfiguration> {
    @NotNull private final Project project;

    private JPanel panel;
    private TextFieldWithBrowseButton testScope;
    private TextFieldWithBrowseButton testerExecutable;
    private RawCommandLineEditor testerOptions;
    private TextFieldWithBrowseButton userSetupScript;

    TesterSettingsEditor(@NotNull final Project project) {
        this.project = project;
    }

    @Override
    protected void resetEditorFrom(@NotNull TesterRunConfiguration runConfiguration) {
        TesterSettings settings = runConfiguration.getSettings();
        testScope.setText(settings.getTestScope());
        testerExecutable.setText(settings.getTesterExecutable());
        testerOptions.setText(settings.getTesterOptions());
        userSetupScript.setText(settings.getSetupScriptPath());
    }

    @Override
    protected void applyEditorTo(@NotNull TesterRunConfiguration runConfiguration) throws ConfigurationException {
        TesterSettings settings = runConfiguration.getSettings();
        settings.setTestScope(testScope.getText());
        settings.setTesterExecutable(testerExecutable.getText());
        settings.setTesterOptions(testerOptions.getText());
        settings.setSetupScriptPath(userSetupScript.getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return panel;
    }

    private void createUIComponents() {
        testScope = new TextFieldWithBrowseButton();
        testScope.addBrowseFolderListener(null, null, project, FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor());

        testerExecutable = new TextFieldWithBrowseButton();
        testerExecutable.addBrowseFolderListener(null, null, project, FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor());

        testerOptions = new RawCommandLineEditor();
        testerOptions.setDialogCaption("Options");

        userSetupScript = new TextFieldWithBrowseButton();
        userSetupScript.addBrowseFolderListener(null, null, project, FileChooserDescriptorFactory.createSingleFileDescriptor("php"));
    }
}
