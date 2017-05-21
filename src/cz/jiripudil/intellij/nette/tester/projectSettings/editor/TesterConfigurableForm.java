package cz.jiripudil.intellij.nette.tester.projectSettings.editor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.JBColor;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.ElementProducer;
import com.jetbrains.php.util.ConfigurableForm;
import cz.jiripudil.intellij.nette.tester.TesterBundle;
import cz.jiripudil.intellij.nette.tester.projectSettings.TesterNamespaceMapping;
import cz.jiripudil.intellij.nette.tester.projectSettings.TesterProjectSettings;
import cz.jiripudil.intellij.nette.tester.projectSettings.TesterProjectSettingsManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

public class TesterConfigurableForm implements ConfigurableForm {
    private final Project project;
    private JPanel panel;

    private ComboBox<String> defaultExtensionCombobox;
    private JPanel namespaceMappingPanel;
    private JBLabel defaultExtensionLabel;

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
        TesterProjectSettings settings = TesterProjectSettingsManager.getInstance(project).getState();
        return settings != null && !(settings.getDefaultExtension().equals(defaultExtensionCombobox.getSelectedItem()) && settings.getNamespaceMappings().equals(namespaceMappingTable.getTableView().getItems()));

    }

    @Override
    public void apply() {
        TesterProjectSettings settings = TesterProjectSettingsManager.getInstance(project).getState();
        if (settings == null) {
            return;
        }

        settings.setDefaultExtension((String) defaultExtensionCombobox.getSelectedItem());

        // lists work with references which complicates detecting modification, cloning each item helps
        settings.setNamespaceMappings(cloneNamespaceMappings(namespaceMappingTable.getTableView().getItems()));
    }

    @Override
    public void reset() {
        TesterProjectSettings settings = TesterProjectSettingsManager.getInstance(project).getState();
        if (settings == null) {
            return;
        }

        defaultExtensionCombobox.setSelectedItem(settings.getDefaultExtension());

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
