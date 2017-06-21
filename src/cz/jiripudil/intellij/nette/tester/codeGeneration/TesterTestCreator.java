package cz.jiripudil.intellij.nette.tester.codeGeneration;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Conditions;
import com.intellij.platform.ProjectBaseDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.testIntegration.TestCreator;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import cz.jiripudil.intellij.nette.tester.TesterUtil;
import org.jetbrains.annotations.Nullable;

public class TesterTestCreator implements TestCreator {
    public static final TesterTestCreator INSTANCE = new TesterTestCreator();

    private TesterTestCreator() {
    }

    @Override
    public boolean isAvailable(Project project, Editor editor, PsiFile psiFile) {
        PhpClass phpClass = findClass(psiFile);
        return phpClass != null;
    }

    @Nullable
    static PhpClass findClass(PsiFile psiFile) {
        if (psiFile instanceof PhpFile) {
            PhpClass phpClass = PhpPsiUtil.findClass((PhpFile) psiFile, Conditions.alwaysTrue());
            if (phpClass != null && !TesterUtil.isTestClass(phpClass)) {
                return phpClass;
            }
        }

        return null;
    }

    @Override
    public void createTest(Project project, Editor editor, PsiFile psiFile) {
        (new TesterNewTestCaseAction(false)).invoke(project, psiFile.getContainingDirectory(), psiFile, null);
    }
}
