package cz.jiripudil.intellij.nette.tester.projectSettings.editor;

import com.intellij.openapi.project.Project;
import com.intellij.ui.EditorTextField;
import com.intellij.util.ui.AbstractTableCellEditor;
import com.jetbrains.php.completion.PhpCompletionUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class NamespaceTableCellEditor extends AbstractTableCellEditor {
    @NotNull private final Project project;

    private JPanel panel;
    private EditorTextField namespaceField;

    NamespaceTableCellEditor(@NotNull final Project project) {
        this.project = project;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        namespaceField.setText(value.toString());
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return namespaceField.getText();
    }

    private void createUIComponents() {
        namespaceField = new EditorTextField("", project, null);
        PhpCompletionUtil.installNamespaceCompletion(namespaceField, null, () -> {});
    }
}
