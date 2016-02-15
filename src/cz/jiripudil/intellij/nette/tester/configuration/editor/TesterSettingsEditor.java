package cz.jiripudil.intellij.nette.tester.configuration.editor;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.RawCommandLineEditor;
import cz.jiripudil.intellij.nette.tester.configuration.TesterRunConfiguration;
import cz.jiripudil.intellij.nette.tester.configuration.settings.TesterSettings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class TesterSettingsEditor extends SettingsEditor<TesterRunConfiguration> {
    private Project project;

    private JPanel panel;
    private TextFieldWithBrowseButton testScope;
    private TextFieldWithBrowseButton testerExecutable;
    private RawCommandLineEditor testerOptions;
    private TextFieldWithBrowseButton phpIniPath;

    public void init(Project project) {
        this.project = project;
    }

    @Override
    protected void resetEditorFrom(TesterRunConfiguration runConfiguration) {
        TesterSettings settings = runConfiguration.getSettings();
        testScope.setText(settings.testScope);
        testerExecutable.setText(settings.testerExecutable);
        testerOptions.setText(settings.testerOptions);
        phpIniPath.setText(settings.phpIniPath);
    }

    @Override
    protected void applyEditorTo(TesterRunConfiguration runConfiguration) throws ConfigurationException {
        TesterSettings settings = runConfiguration.getSettings();
        settings.testScope = testScope.getText();
        settings.testerExecutable = testerExecutable.getText();
        settings.testerOptions = testerOptions.getText();
        settings.phpIniPath = phpIniPath.getText();
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

        phpIniPath = new TextFieldWithBrowseButton();
        phpIniPath.addBrowseFolderListener(null, null, null, FileChooserDescriptorFactory.createSingleFileDescriptor("ini"));
    }
}
