package cz.jiripudil.intellij.nette.tester.codeGeneration;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.actions.CodeInsightAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.psi.PhpCodeEditUtil;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import cz.jiripudil.intellij.nette.tester.TesterUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class TesterAbstractGenerateMethodAction extends CodeInsightAction implements CodeInsightActionHandler {
    @NotNull
    @Override
    protected CodeInsightActionHandler getHandler() {
        return this;
    }

    @Override
    protected boolean isValidForFile(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        return findTestClass(editor, file) != null;
    }

    @Nullable
    private static PhpClass findTestClass(@NotNull Editor editor, @NotNull PsiFile file) {
        PhpClass testClass = PhpCodeEditUtil.findClassAtCaret(editor, file);
        return testClass != null && TesterUtil.isTestClass(testClass) ? testClass : null;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile psiFile) {
        PhpClass phpClass = findTestClass(editor, psiFile);
        Method method = PhpPsiElementFactory.createMethod(project, "public function testFoo(){}");

        assert phpClass != null;

        PsiElement element = PhpCodeEditUtil.insertClassMember(phpClass, method);
        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
        TextRange range = element.getTextRange();
        editor.getDocument().replaceString(range.getStartOffset(), range.getEndOffset(), "");
        editor.getCaretModel().moveToOffset(range.getStartOffset());
        insertMethod(project, editor);
    }

    abstract protected void insertMethod(@NotNull Project project, @NotNull Editor editor);
}
