package cz.jiripudil.intellij.nette.tester.lineMarker;

import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import cz.jiripudil.intellij.nette.tester.TesterIcons;
import cz.jiripudil.intellij.nette.tester.action.TesterCreateMethodRunTestAction;
import cz.jiripudil.intellij.nette.tester.action.TesterMethodRunTestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TesterMethodRunLineMarkerProvider extends RunLineMarkerContributor {
    @Override
    public @Nullable Info getInfo(@NotNull PsiElement psiElement) {
        if (psiElement instanceof Method) {
            PhpClass phpClass = ((Method) psiElement).getContainingClass();
            String className = phpClass != null ? phpClass.getName() : "null";
            String methodName = ((Method) psiElement).getName();

            AnAction[] actions = new AnAction[2];
            actions[0] = new TesterMethodRunTestAction(psiElement, className, methodName);
            actions[1] = new TesterCreateMethodRunTestAction(psiElement, className, methodName);

            return new Info(TesterIcons.RUN_METHOD, actions, RunLineMarkerContributor.RUN_TEST_TOOLTIP_PROVIDER);
        }
        return null;
    }

}