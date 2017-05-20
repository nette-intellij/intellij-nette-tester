package cz.jiripudil.intellij.nette.tester.configuration;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;

public class TesterRunConfigurationProducer extends RunConfigurationProducer<TesterRunConfiguration> {
    protected TesterRunConfigurationProducer() {
        super(TesterConfigurationType.getInstance());
    }

    @Override
    protected boolean setupConfigurationFromContext(TesterRunConfiguration runConfiguration, ConfigurationContext configurationContext, Ref<PsiElement> ref) {
        PsiElement location = configurationContext.getPsiLocation();
        if (!(location instanceof PsiDirectory)) {
            return false;
        }

        PsiDirectory directory = (PsiDirectory) location;
        runConfiguration.getSettings().setTestScope(directory.getVirtualFile().getPresentableUrl());
        runConfiguration.setGeneratedName(runConfiguration.suggestedName());
        return true;
    }

    @Override
    public boolean isConfigurationFromContext(TesterRunConfiguration runConfiguration, ConfigurationContext configurationContext) {
        PsiElement location = configurationContext.getPsiLocation();
        if (!(location instanceof PsiDirectory)) {
            return false;
        }

        PsiDirectory directory = (PsiDirectory) location;
        return runConfiguration.getSettings().getTestScope().equals(directory.getVirtualFile().getPresentableUrl());
    }
}
