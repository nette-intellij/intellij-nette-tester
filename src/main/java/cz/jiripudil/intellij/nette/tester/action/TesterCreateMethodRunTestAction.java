package cz.jiripudil.intellij.nette.tester.action;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.psi.PsiElement;
import cz.jiripudil.intellij.nette.tester.TesterBundle;
import cz.jiripudil.intellij.nette.tester.TesterIcons;
import cz.jiripudil.intellij.nette.tester.TesterUtil;
import cz.jiripudil.intellij.nette.tester.configuration.TesterRunConfiguration;
import cz.jiripudil.intellij.nette.tester.configuration.TesterTestMethodRunConfiguration;
import cz.jiripudil.intellij.nette.tester.configuration.TesterTestMethodRunConfigurationType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class TesterCreateMethodRunTestAction extends BaseTesterRunAction {
    private final String path;
    private final String methodName;
    private final String testName;

    public TesterCreateMethodRunTestAction(@NotNull PsiElement element, @NotNull String testName, @NotNull String testMethod) {
        super(element.getProject());

        this.path = element.getContainingFile().getVirtualFile().getPath();
        this.testName = testName;
        this.methodName = testMethod;

        getTemplatePresentation().setText(TesterBundle.message("action.createRunTestAction.name") + " '" + testMethod + "()'...");
        getTemplatePresentation().setIcon(TesterIcons.TESTER_CONFIG);
    }

    @Nullable
    protected RunnerAndConfigurationSettings prepareAction() {
        wasCreated = false;
        List<TesterTestMethodRunConfiguration> configurations = TesterUtil.getMethodRunConfigurations(project);
        List<TesterTestMethodRunConfiguration> samePath = configurations.stream()
                .filter(configuration -> {
                    if (configuration.getMethod() == null) {
                        return false;
                    }

                    try {
                        configuration.checkConfiguration();

                    } catch (RuntimeConfigurationException ex) {
                        return false;
                    }
                    return path.equals(configuration.getSettings().getPath())
                            && configuration.getMethod().startsWith(methodName);
                })
                .collect(Collectors.toList());

        RunManager manager = RunManager.getInstance(project);
        if (samePath.size() == 0) {
            TesterRunConfiguration mainConfiguration = TesterUtil.getMainConfiguration(project);
            if (mainConfiguration == null) {
                return null;
            }

            ConfigurationFactory factory = TesterTestMethodRunConfigurationType.createFactory();
            TesterTestMethodRunConfiguration current = new TesterTestMethodRunConfiguration(
                    project,
                    factory,
                    getConfigurationName()
            );
            current.getSettings().setPath(path);
            current.setMethodName(methodName);

            //todo: get more information from mainConfiguration and save to method configuration
            current.getSettings().setCommandLineSettings(mainConfiguration.getSettings().getPhpCommandLineSettings());

            RunnerAndConfigurationSettings action = manager.createConfiguration(current, factory);
            action.setTemporary(isTemporary());

            manager.addConfiguration(action);
            manager.setSelectedConfiguration(action);
            wasCreated = true;
            return action;

        } else {
            TesterTestMethodRunConfiguration existing = samePath.get(0);
            if (existing.getFactory() == null) {
                return null;
            }
            RunnerAndConfigurationSettings action = manager.createConfiguration(existing, existing.getFactory());
            manager.setSelectedConfiguration(action);
            return action;
        }
    }

    private String getConfigurationName() {
        return "tests:" + testName + ":" + TesterTestMethodRunConfiguration.createSuggestedName(methodName).trim();
    }
}
