package cz.jiripudil.intellij.nette.tester.action;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.psi.PsiElement;
import cz.jiripudil.intellij.nette.tester.TesterBundle;
import cz.jiripudil.intellij.nette.tester.TesterIcons;
import cz.jiripudil.intellij.nette.tester.TesterUtil;
import cz.jiripudil.intellij.nette.tester.configuration.TesterRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class TesterCreateRunTestAction extends BaseTesterRunAction {
    private final String path;
    private final String testName;

    public TesterCreateRunTestAction(@NotNull PsiElement element, @NotNull String testName) {
        super(element.getProject());
        this.path = element.getContainingFile().getVirtualFile().getPath();
        this.testName = testName;

        getTemplatePresentation().setText(TesterBundle.message("action.createRunTestAction.name") + " '" + testName + "'...");
        getTemplatePresentation().setIcon(TesterIcons.TESTER_CONFIG);
    }

    @Nullable
    protected RunnerAndConfigurationSettings prepareAction() {
        wasCreated = false;
        List<TesterRunConfiguration> configurations = TesterUtil.getRunConfigurations(getProject());
        List<TesterRunConfiguration> samePath = configurations.stream()
                .filter(configuration -> {
                    try {
                        configuration.checkConfiguration();

                    } catch (RuntimeConfigurationException ex) {
                        return false;
                    }
                    return path.equals(configuration.getSettings().getTestScope());
                })
                .collect(Collectors.toList());

        RunManager manager = RunManager.getInstance(getProject());
        if (samePath.size() == 0) {
            TesterRunConfiguration mainConfiguration = TesterUtil.getMainConfiguration(getProject(), configurations);
            if (mainConfiguration == null || mainConfiguration.getFactory() == null) {
                return null;
            }

            TesterRunConfiguration current = (TesterRunConfiguration) mainConfiguration.clone();
            current.setName("tests:" + testName);
            current.getSettings().setTestScope(path);
            RunnerAndConfigurationSettings action = manager.createConfiguration(current, mainConfiguration.getFactory());
            action.setTemporary(isTemporary());

            manager.addConfiguration(action);
            manager.setSelectedConfiguration(action);
            wasCreated = true;
            return action;

        } else {
            TesterRunConfiguration existing = samePath.get(0);
            if (existing.getFactory() == null) {
                return null;
            }

            RunnerAndConfigurationSettings action = manager.createConfiguration(existing, existing.getFactory());
            manager.setSelectedConfiguration(action);
            return action;
        }
    }
}
