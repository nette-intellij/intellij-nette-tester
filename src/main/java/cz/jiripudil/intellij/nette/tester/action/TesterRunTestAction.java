package cz.jiripudil.intellij.nette.tester.action;

import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiElement;
import cz.jiripudil.intellij.nette.tester.TesterBundle;
import cz.jiripudil.intellij.nette.tester.TesterIcons;
import org.jetbrains.annotations.NotNull;

public class TesterRunTestAction extends TesterCreateRunTestAction {

    public TesterRunTestAction(@NotNull PsiElement element, @NotNull String testName) {
        super(element, testName);

        getTemplatePresentation().setText(TesterBundle.message("action.runTestAction.name") + " '" + testName + "'");
        getTemplatePresentation().setIcon(TesterIcons.RUN);
    }

    protected boolean isTemporary() {
        return true;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        RunnerAndConfigurationSettings forRun = prepareAction();
        if (forRun != null) {
            ProgramRunnerUtil.executeConfiguration(forRun, DefaultRunExecutor.getRunExecutorInstance());
        }
    }
}
