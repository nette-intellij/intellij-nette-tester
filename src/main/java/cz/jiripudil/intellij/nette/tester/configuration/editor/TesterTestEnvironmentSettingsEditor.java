package cz.jiripudil.intellij.nette.tester.configuration.editor;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.jetbrains.php.config.PhpProjectConfigurationFacade;
import com.jetbrains.php.config.interpreters.PhpConfigurationOptionsComponent;
import com.jetbrains.php.config.interpreters.PhpInterpreter;
import com.jetbrains.php.config.interpreters.PhpInterpreterComboBox;
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl;
import cz.jiripudil.intellij.nette.tester.TesterBundle;
import cz.jiripudil.intellij.nette.tester.configuration.TesterRunConfiguration;
import cz.jiripudil.intellij.nette.tester.configuration.TesterSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TesterTestEnvironmentSettingsEditor extends SettingsEditor<TesterRunConfiguration> {
    @NotNull private final Project project;

    private JPanel panel;
    private PhpInterpreterComboBox phpInterpreter;
    private PhpConfigurationOptionsComponent interpreterOptions;
    private TextFieldWithBrowseButton phpIniPath;
    private JBCheckBox useSystemPhpIniCheckbox;

    private JBLabel interpreterLabel;
    private JBLabel interpreterOptionsLabel;
    private JBLabel pathToPhpIniLabel;

    TesterTestEnvironmentSettingsEditor(@NotNull final Project project) {
        super();
        this.project = project;
        useSystemPhpIniCheckbox.addItemListener(e -> phpIniPath.setEnabled(!useSystemPhpIniCheckbox.isSelected()));
    }

    @Override
    protected void resetEditorFrom(@NotNull TesterRunConfiguration runConfiguration) {
        TesterSettings settings = runConfiguration.getSettings();

        PhpInterpreter interpreter = settings.getPhpInterpreter(project);
        phpInterpreter.reset(PhpInterpretersManagerImpl.getInstance(project).findInterpreterName(interpreter != null ? interpreter.getId() : null));
        interpreterOptions.setConfigurationOptions(settings.getPhpInterpreterOptions());
        phpIniPath.setText(settings.getPhpIniPath());
        useSystemPhpIniCheckbox.setSelected(settings.getUseSystemPhpIni());
    }

    @Override
    protected void applyEditorTo(@NotNull TesterRunConfiguration runConfiguration) throws ConfigurationException {
        TesterSettings settings = runConfiguration.getSettings();

        PhpInterpreter interpreter = getSelectedInterpreter();
        settings.setPhpInterpreterId(interpreter != null ? interpreter.getId() : null);
        settings.setPhpInterpreterOptions(interpreterOptions.getConfigurationOptionsData());
        settings.setPhpIniPath(phpIniPath.getText());
        settings.setUseSystemPhpIni(useSystemPhpIniCheckbox.isSelected());
    }

    @Nullable
    private PhpInterpreter getSelectedInterpreter() {
        String interpreterName = phpInterpreter.getSelectedItemName();
        if (interpreterName == null) {
            return PhpProjectConfigurationFacade.getInstance(project).getInterpreter();
        }

        return PhpInterpretersManagerImpl.getInstance(project).findInterpreter(interpreterName);
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return panel;
    }

    private void createUIComponents() {
        phpInterpreter = new PhpInterpreterComboBox(project, null);
        phpInterpreter.setModel(PhpInterpretersManagerImpl.getInstance(project).getInterpreters(), null);
        phpInterpreter.setNoItemText("<default project interpreter>");

        interpreterOptions = new PhpConfigurationOptionsComponent();

        phpIniPath = new TextFieldWithBrowseButton();
        phpIniPath.addBrowseFolderListener(null, null, project, FileChooserDescriptorFactory.createSingleFileDescriptor("ini"));

        useSystemPhpIniCheckbox = new JBCheckBox(TesterBundle.message("runConfiguration.editor.testEnv.useSystemPhpIni"));

        interpreterLabel = new JBLabel(TesterBundle.message("runConfiguration.editor.testEnv.interpreter"));
        interpreterOptionsLabel = new JBLabel(TesterBundle.message("runConfiguration.editor.testEnv.interpreterOptions"));
        pathToPhpIniLabel = new JBLabel(TesterBundle.message("runConfiguration.editor.testEnv.phpIni"));
    }
}
