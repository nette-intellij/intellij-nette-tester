package cz.jiripudil.intellij.nette.tester.projectSettings.editor;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.ElementProducer;
import com.jetbrains.php.util.ConfigurableForm;
import cz.jiripudil.intellij.nette.tester.TesterBundle;
import cz.jiripudil.intellij.nette.tester.TesterUtil;
import cz.jiripudil.intellij.nette.tester.projectSettings.TesterNamespaceMapping;
import cz.jiripudil.intellij.nette.tester.projectSettings.TesterProjectSettings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

public class TesterConfigurableForm implements ConfigurableForm {
    private final Project project;
    private JPanel panel;

    private JBLabel defaultExtensionLabel;
    private ComboBox<String> defaultExtensionCombobox;
    private JBLabel bootstrapFileLabel;
    private TextFieldWithBrowseButton bootstrapFileField;

    private JPanel namespaceMappingPanel;
    private ComboBox<String> testerVersionCombobox;
    private JBLabel testerVersionLabel;
    private NamespaceMappingTable namespaceMappingTable;

    public TesterConfigurableForm(@NotNull final Project project) {
        this.project = project;
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return panel;
    }

    @Override
    public boolean isModified() {
        TesterProjectSettings settings = TesterUtil.getTesterSettings(project);
        return settings != null && !(
            settings.getDefaultExtension().equals(defaultExtensionCombobox.getSelectedItem())
            && settings.getTesterVersion().equals(testerVersionCombobox.getSelectedItem())
            && StringUtil.notNullize(settings.getBootstrapFile()).equals(bootstrapFileField.getText())
            && settings.getNamespaceMappings().equals(namespaceMappingTable.getTableView().getItems())
        );

    }

    @Override
    public void apply() {
        TesterProjectSettings settings = TesterUtil.getTesterSettings(project);
        if (settings == null) {
            return;
        }

        settings.setDefaultExtension((String) defaultExtensionCombobox.getSelectedItem());
        settings.setTesterVersion((String) testerVersionCombobox.getSelectedItem());
        settings.setBootstrapFile(bootstrapFileField.getText());

        // lists work with references which complicates detecting modification, cloning each item helps
        settings.setNamespaceMappings(cloneNamespaceMappings(namespaceMappingTable.getTableView().getItems()));
    }

    @Override
    public void reset() {
        TesterProjectSettings settings = TesterUtil.getTesterSettings(project);
        if (settings == null) {
            return;
        }

        defaultExtensionCombobox.setSelectedItem(settings.getDefaultExtension());
        testerVersionCombobox.setSelectedItem(settings.getTesterVersion());
        bootstrapFileField.setText(settings.getBootstrapFile());

        // lists work with references which complicates detecting modification, cloning each item helps
        namespaceMappingTable.getTableView().getTableViewModel().setItems(cloneNamespaceMappings(settings.getNamespaceMappings()));
    }

    private List<TesterNamespaceMapping> cloneNamespaceMappings(List<TesterNamespaceMapping> input) {
        return input.stream()
            .map(TesterNamespaceMapping::clone)
            .collect(Collectors.toList());
    }

    private void createUIComponents() {
        defaultExtensionLabel = new JBLabel(TesterBundle.message("settings.defaultExtension"));
        defaultExtensionCombobox = new ComboBox<>(new String[]{"phpt", "php"});

        testerVersionLabel = new JBLabel(TesterBundle.message("settings.testerVersion"));
        testerVersionCombobox = new ComboBox<>(new String[]{"< 2.0", "> 2.0"});

        bootstrapFileLabel = new JBLabel(TesterBundle.message("settings.bootstrapFile"));
        bootstrapFileField = new TextFieldWithBrowseButton();
        bootstrapFileField.addBrowseFolderListener(null, null, project, FileChooserDescriptorFactory.createSingleFileDescriptor("php"));

        namespaceMappingTable = new NamespaceMappingTable(project);
        namespaceMappingPanel = ToolbarDecorator.createDecorator(namespaceMappingTable.getTableView(), new ElementProducer<TesterNamespaceMapping>() {
            @Override
            public TesterNamespaceMapping createElement() {
                return new TesterNamespaceMapping();
            }

            @Override
            public boolean canCreateElement() {
                return true;
            }
        }).createPanel();

        namespaceMappingPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, JBColor.LIGHT_GRAY), TesterBundle.message("settings.namespaceMappings.title")));
    }
}
