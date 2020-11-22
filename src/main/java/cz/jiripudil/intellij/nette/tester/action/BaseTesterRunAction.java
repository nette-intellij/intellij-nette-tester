package cz.jiripudil.intellij.nette.tester.action;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.ide.actions.runAnything.RunAnythingAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import cz.jiripudil.intellij.nette.tester.TesterBundle;
import cz.jiripudil.intellij.nette.tester.TesterUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class BaseTesterRunAction extends RunAnythingAction {

    protected Project project;
    protected boolean wasCreated = false;

    BaseTesterRunAction(@NotNull Project project) {
        this.project = project;
    }

    @Nullable
    abstract protected RunnerAndConfigurationSettings prepareAction();

    protected boolean isTemporary() {
        return false;
    }

    protected Project getProject() {
        return project;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        prepareAction();

        String title;
        String description;
        if (!wasCreated) {
            title = TesterBundle.message("runConfiguration.mainConfiguration.alreadyCreated.title");
            description = TesterBundle.message("runConfiguration.mainConfiguration.alreadyCreated.description");
        } else {
            title = TesterBundle.message("runConfiguration.mainConfiguration.created.title");
            description = TesterBundle.message("runConfiguration.mainConfiguration.created.description");
        }
        TesterUtil.doNotify(title, description, NotificationType.INFORMATION, project);
    }
}
