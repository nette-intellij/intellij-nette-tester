package cz.jiripudil.intellij.nette.tester.lineMarker;

import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import cz.jiripudil.intellij.nette.tester.TesterIcons;
import cz.jiripudil.intellij.nette.tester.action.TesterCreateMethodRunTestAction;
import cz.jiripudil.intellij.nette.tester.action.TesterMethodRunTestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TesterMethodRunLineMarkerProvider extends RunLineMarkerContributor {
    static String TEST_METHOD_PREFIX = "test";

    @Override
    public @Nullable Info getInfo(@NotNull PsiElement psiElement) {
        if (psiElement.getNode().getElementType() == PhpTokenTypes.IDENTIFIER) {
            PsiElement method = psiElement.getParent();
            if (method instanceof Method) {
                String methodName = ((Method) method).getName();
                if (!methodName.startsWith(TEST_METHOD_PREFIX)) {
                    return null;
                }

                PhpClass phpClass = ((Method) method).getContainingClass();
                String className = phpClass != null ? phpClass.getName() : "null";

                if (psiElement.getText().equals(methodName)) {
                    AnAction[] actions = new AnAction[2];
                    actions[0] = new TesterMethodRunTestAction(psiElement, className, methodName);
                    actions[1] = new TesterCreateMethodRunTestAction(psiElement, className, methodName);

                    return new Info(TesterIcons.RUN_METHOD, actions, RunLineMarkerContributor.RUN_TEST_TOOLTIP_PROVIDER);
                }
            }
        }
        return null;
    }

}