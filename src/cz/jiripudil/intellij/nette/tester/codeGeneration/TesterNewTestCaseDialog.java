package cz.jiripudil.intellij.nette.tester.codeGeneration;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.UIUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.actions.PhpBaseNewClassDialog;
import com.jetbrains.php.completion.PhpCompletionUtil;
import com.jetbrains.php.lang.PhpLangUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.roots.ui.PhpNamespaceComboBox;
import com.jetbrains.php.roots.ui.PhpPsrDirectoryComboBox;
import com.jetbrains.php.ui.PhpUiUtil;
import cz.jiripudil.intellij.nette.tester.projectSettings.TesterProjectSettings;
import cz.jiripudil.intellij.nette.tester.projectSettings.TesterProjectSettingsManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Properties;

public class TesterNewTestCaseDialog extends PhpBaseNewClassDialog {
    private JPanel contentPane;

    private EditorTextField testTargetTextField;
    private JBLabel testTargetCompletionHint;

    private EditorTextField nameTextField;
    private PhpNamespaceComboBox namespaceComboBox;
    private JBLabel namespaceCompletionHint;

    private EditorTextField fileNameTextField;
    private PhpPsrDirectoryComboBox directoryComboBox;
    private JBLabel directoryCompletionHint;

    TesterNewTestCaseDialog(@NotNull Project project, @Nullable PsiDirectory directory, @Nullable PsiFile file) {
        super(project, directory);
        this.init(contentPane, nameTextField, namespaceComboBox, namespaceCompletionHint, fileNameTextField, directoryComboBox, directoryCompletionHint);

        testTargetTextField.addDocumentListener(new DocumentAdapter() {
            @Override
            public void documentChanged(DocumentEvent e) {
                TesterNewTestCaseDialog.this.addUpdateRequest(() -> {
                    String text = TesterNewTestCaseDialog.this.testTargetTextField.getText();
                    if (!StringUtil.isEmpty(text) && !StringUtil.endsWith(text, "\\")) {
                        PhpIndex instance = PhpIndex.getInstance(TesterNewTestCaseDialog.this.getProject());
                        Collection<PhpClass> classes = instance.getClassesByFQN(text);
                        if (!classes.isEmpty()) {
                            TesterNewTestCaseDialog.this.nameTextField.setText(PhpLangUtil.toShortName(text) + "Test");
                            TesterNewTestCaseDialog.this.fileNameTextField.setText(PhpLangUtil.toShortName(text) + "Test");

                            PhpClass phpClass = classes.iterator().next();
                            String namespace = TesterNamespaceMapper.getInstance(project).mapSourceNamespaceToTestNamespace(phpClass);
                            TesterNewTestCaseDialog.this.namespaceComboBox.getEditorTextField().setText(namespace);
                        }
                    }
                });
            }
        });
        PhpCompletionUtil.installClassCompletion(testTargetTextField, null, this.getDisposable());

        String codeCompletionShortcut = PhpUiUtil.getShortcutTextByActionName("CodeCompletion");
        testTargetCompletionHint.setText("Use " + codeCompletionShortcut + " for class reference completion");
        namespaceCompletionHint.setText("Use " + codeCompletionShortcut + " for namespace completion");
        directoryCompletionHint.setText("Use " + codeCompletionShortcut + " for path completion");

        this.addUpdateRequest(() -> {
            PhpClass phpClass = TesterTestCreator.findClass(file);
            if (phpClass != null) {
                String fqn = phpClass.getFQN();
                this.testTargetTextField.setText(PhpLangUtil.toName(fqn));
                return;
            }

            this.nameTextField.setText("Test");
            Editor editor = this.nameTextField.getEditor();
            if (editor != null) {
                editor.getCaretModel().moveToOffset(0);
            }
        });

        this.init();
    }

    @Override
    protected void init() {
        super.init();
        this.setTitle("Create Nette Tester TestCase");
    }

    @Nullable
    @Override
    protected String getDimensionServiceKey() {
        return "cz.jiripudil.intellij.nette.tester.codeGeneration.TesterNewTestCaseDialog";
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return testTargetTextField;
    }

    @NotNull
    @Override
    public String getTemplateName() {
        return "Tester TestCase";
    }

    @NotNull
    @Override
    protected String getExtension() {
        TesterProjectSettings settings = TesterProjectSettingsManager.getInstance(getProject()).getState();
        return settings != null ? settings.getDefaultExtension() : "phpt";
    }

    @NotNull
    @Override
    public Properties getProperties(@NotNull PsiDirectory directory) {
        Properties properties = super.getProperties(directory);
        String fqn = PhpLangUtil.toName(testTargetTextField.getText());
        String testedName = PhpLangUtil.toShortName(fqn);

        if (StringUtil.isNotEmpty(testedName)) {
            properties.setProperty("TESTED_NAME", testedName);
        }

        String namespace = PhpLangUtil.getParentQualifiedName(fqn);
        if (!PhpLangUtil.isGlobalNamespaceName(namespace)) {
            properties.setProperty("TESTED_NAMESPACE", namespace);
        }

        return properties;
    }

    private void createUIComponents() {
        testTargetTextField = new EditorTextField("", getProject(), FileTypes.PLAIN_TEXT);
        namespaceComboBox = new PhpNamespaceComboBox(getProject(), "", getDisposable());
        directoryComboBox = new PhpPsrDirectoryComboBox(getProject()) {
            @Override
            public void init(@NotNull VirtualFile baseDir, @NotNull String namespace) {
                super.init(baseDir, namespace);
                ProjectFileIndex index = ProjectRootManager.getInstance(TesterNewTestCaseDialog.this.getProject()).getFileIndex();
                if (index.isInSourceContent(baseDir)) {
                    this.setDirectoriesFilter(index::isInTestSourceContent);
                }

                this.updateDirectories(TesterNewTestCaseDialog.this.getNamespace());
            }
        };

        testTargetCompletionHint = new JBLabel(UIUtil.ComponentStyle.MINI);
        namespaceCompletionHint = new JBLabel(UIUtil.ComponentStyle.MINI);
        directoryCompletionHint = new JBLabel(UIUtil.ComponentStyle.MINI);
    }
}
