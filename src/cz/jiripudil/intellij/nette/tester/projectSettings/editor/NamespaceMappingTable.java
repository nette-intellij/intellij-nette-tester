package cz.jiripudil.intellij.nette.tester.projectSettings.editor;

import com.intellij.execution.util.ListTableWithButtons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import cz.jiripudil.intellij.nette.tester.projectSettings.TesterNamespaceMapping;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.TableCellEditor;

public class NamespaceMappingTable extends ListTableWithButtons<TesterNamespaceMapping> {
    @NotNull private final Project project;

    NamespaceMappingTable(@NotNull final Project project) {
        super();
        this.project = project;
        this.getTableView().getEmptyText().setText("No mappings");
    }

    @Override
    protected ListTableModel createListModel() {
        ColumnInfo sourceNamespace = new ElementsColumnInfoBase<TesterNamespaceMapping>("Source namespace") {
            @NotNull
            @Override
            public String valueOf(TesterNamespaceMapping testerTestMapping) {
                return testerTestMapping.getSourceNamespace();
            }

            @Override
            public boolean isCellEditable(TesterNamespaceMapping testerTestMapping) {
                return NamespaceMappingTable.this.canDeleteElement(testerTestMapping);
            }

            @Override
            public void setValue(TesterNamespaceMapping testerTestMapping, String value) {
                testerTestMapping.setSourceNamespace(value);
            }

            @NotNull
            @Override
            protected String getDescription(TesterNamespaceMapping testerTestMapping) {
                return valueOf(testerTestMapping);
            }

            @NotNull
            @Override
            public TableCellEditor getEditor(TesterNamespaceMapping testerTestMapping) {
                return new NamespaceTableCellEditor(project);
            }
        };

        ColumnInfo testNamespace = new ElementsColumnInfoBase<TesterNamespaceMapping>("Tests namespace") {
            @NotNull
            @Override
            public String valueOf(TesterNamespaceMapping testerTestMapping) {
                return testerTestMapping.getTestsNamespace();
            }

            @Override
            public boolean isCellEditable(TesterNamespaceMapping testerTestMapping) {
                return NamespaceMappingTable.this.canDeleteElement(testerTestMapping);
            }

            @Override
            public void setValue(TesterNamespaceMapping testerTestMapping, String value) {
                testerTestMapping.setTestsNamespace(value);
            }

            @NotNull
            @Override
            protected String getDescription(TesterNamespaceMapping testerTestMapping) {
                return valueOf(testerTestMapping);
            }

            @NotNull
            @Override
            public TableCellEditor getEditor(TesterNamespaceMapping testerTestMapping) {
                return new NamespaceTableCellEditor(project);
            }
        };

        return new ListTableModel(sourceNamespace, testNamespace);
    }

    @Override
    protected TesterNamespaceMapping createElement() {
        return new TesterNamespaceMapping();
    }

    @Override
    protected boolean isEmpty(TesterNamespaceMapping testerTestMapping) {
        return StringUtil.isEmpty(testerTestMapping.getSourceNamespace())
            && StringUtil.isEmpty(testerTestMapping.getTestsNamespace());
    }

    @Override
    protected TesterNamespaceMapping cloneElement(TesterNamespaceMapping testerTestMapping) {
        return testerTestMapping.clone();
    }

    @Override
    protected boolean canDeleteElement(TesterNamespaceMapping testerTestMapping) {
        return true;
    }
}
