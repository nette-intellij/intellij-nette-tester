package cz.jiripudil.intellij.nette.tester.codeGeneration;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
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
import cz.jiripudil.intellij.nette.tester.TesterBundle;
import cz.jiripudil.intellij.nette.tester.TesterUtil;
import cz.jiripudil.intellij.nette.tester.projectSettings.TesterProjectSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private JBLabel classToTestLabel;
    private JBLabel testClassLabel;
    private JBLabel namespaceLabel;
    private JBLabel fileNameLabel;
    private JBLabel directoryLabel;

    TesterNewTestCaseDialog(@NotNull Project project, @Nullable PsiDirectory directory, @Nullable PsiFile file, boolean fixedDirectory) {
        super(project, directory);
        this.init(contentPane, nameTextField, namespaceComboBox, namespaceCompletionHint, fileNameTextField, directoryComboBox, directoryCompletionHint);

        testTargetTextField.addDocumentListener(new DocumentListener() {
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

                            if (!fixedDirectory) {
                                PhpClass phpClass = classes.iterator().next();
                                String namespace = TesterNamespaceMapper.getInstance(project).mapSourceNamespaceToTestNamespace(phpClass);
                                TesterNewTestCaseDialog.this.namespaceComboBox.getEditorTextField().setText(namespace);
                            }
                        }
                    }
                });
            }
        });
        PhpCompletionUtil.installClassCompletion(testTargetTextField, null, this.getDisposable(), null);

        String codeCompletionShortcut = PhpUiUtil.getShortcutTextByActionName("CodeCompletion");
        testTargetCompletionHint.setText(TesterBundle.message("dialog.newTestCase.completionShortcut", codeCompletionShortcut, "class reference"));
        namespaceCompletionHint.setText(TesterBundle.message("dialog.newTestCase.completionShortcut", codeCompletionShortcut, "namespace"));
        directoryCompletionHint.setText(TesterBundle.message("dialog.newTestCase.completionShortcut", codeCompletionShortcut, "path"));

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
        this.setTitle(TesterBundle.message("dialog.newTestCase.title"));
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
    public String getExtension() {
        TesterProjectSettings settings = TesterUtil.getTesterSettings(getProject());
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

        // bootstrap
        TesterProjectSettings settings = TesterUtil.getTesterSettings(getProject());
        if (settings != null && settings.getBootstrapFile() != null) {
            VirtualFile bootstrapFile = LocalFileSystem.getInstance().findFileByPath(settings.getBootstrapFile());
            if (bootstrapFile != null && getDirectory() != null) {
                Path bootstrapFilePath = Paths.get(bootstrapFile.getPath());
                Path testDirectoryPath = Paths.get(directoryComboBox.getSelectedPath());

                Path bootstrapRelativePath = testDirectoryPath.relativize(bootstrapFilePath);
                properties.setProperty("BOOTSTRAP_RELATIVE_PATH", bootstrapRelativePath.toString());
            }
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
                this.setDirectoriesFilter(index::isInTestSourceContent);

                this.updateDirectories(TesterNewTestCaseDialog.this.getNamespace());
            }
        };

        classToTestLabel = new JBLabel(TesterBundle.message("dialog.newTestCase.label.classToTest"));
        testClassLabel = new JBLabel(TesterBundle.message("dialog.newTestCase.label.testClass"));
        namespaceLabel = new JBLabel(TesterBundle.message("dialog.newTestCase.label.namespace"));
        fileNameLabel = new JBLabel(TesterBundle.message("dialog.newTestCase.label.fileName"));
        directoryLabel = new JBLabel(TesterBundle.message("dialog.newTestCase.label.directory"));

        testTargetCompletionHint = new JBLabel(UIUtil.ComponentStyle.MINI);
        namespaceCompletionHint = new JBLabel(UIUtil.ComponentStyle.MINI);
        directoryCompletionHint = new JBLabel(UIUtil.ComponentStyle.MINI);
    }
}
