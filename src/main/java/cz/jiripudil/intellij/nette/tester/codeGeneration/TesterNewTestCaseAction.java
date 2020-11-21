package cz.jiripudil.intellij.nette.tester.codeGeneration;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.actions.PhpNewBaseAction;
import com.jetbrains.php.templates.PhpCreateFileFromTemplateDataProvider;
import cz.jiripudil.intellij.nette.tester.TesterBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TesterNewTestCaseAction extends PhpNewBaseAction {
    private final boolean fixedDirectory;

    public TesterNewTestCaseAction() {
        this(true);
    }

    TesterNewTestCaseAction(boolean fixedDirectory) {
        super(TesterBundle.message("action.newTestCase.name"), TesterBundle.message("action.newTestCase.description"), PhpIcons.PHP_TEST_FILE);
        this.fixedDirectory = fixedDirectory;
    }

    @Nullable
    @Override
    protected PhpCreateFileFromTemplateDataProvider getDataProvider(@NotNull Project project, @NotNull PsiDirectory psiDirectory, @Nullable PsiFile psiFile) {
        TesterNewTestCaseDialog dialog = new TesterNewTestCaseDialog(project, psiDirectory, psiFile, fixedDirectory);
        return dialog.showAndGet() ? dialog : null;
    }
}
