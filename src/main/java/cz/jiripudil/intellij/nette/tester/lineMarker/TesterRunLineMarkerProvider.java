package cz.jiripudil.intellij.nette.tester.lineMarker;

import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import cz.jiripudil.intellij.nette.tester.TesterIcons;
import cz.jiripudil.intellij.nette.tester.TesterUtil;
import cz.jiripudil.intellij.nette.tester.action.TesterCreateRunTestAction;
import cz.jiripudil.intellij.nette.tester.action.TesterRunTestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TesterRunLineMarkerProvider extends RunLineMarkerContributor {
    @Override
    public @Nullable Info getInfo(@NotNull PsiElement psiElement) {
        if (psiElement.getNode().getElementType() == PhpTokenTypes.IDENTIFIER) {
            PsiElement phpClass = psiElement.getParent();
            if (!(phpClass instanceof PhpClass) || !TesterUtil.isTestClass((PhpClass) phpClass)) {
                return null;
            }

            String className = psiElement.getText();
            if (((PhpClass) phpClass).getName().equals(className)) {
                AnAction[] actions = new AnAction[2];
                actions[0] = new TesterRunTestAction(psiElement, className);
                actions[1] = new TesterCreateRunTestAction(psiElement, className);

                return new Info(TesterIcons.RUN_CLASS, actions, RunLineMarkerContributor.RUN_TEST_TOOLTIP_PROVIDER);
            }
        }
        return null;
    }

}