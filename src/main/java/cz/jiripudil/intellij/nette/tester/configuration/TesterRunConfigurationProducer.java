package cz.jiripudil.intellij.nette.tester.configuration;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import cz.jiripudil.intellij.nette.tester.TesterUtil;

public class TesterRunConfigurationProducer extends RunConfigurationProducer<TesterRunConfiguration> {
    protected TesterRunConfigurationProducer() {
        super(TesterRunConfigurationType.getInstance());
    }

    @Override
    protected boolean setupConfigurationFromContext(TesterRunConfiguration runConfiguration, ConfigurationContext configurationContext, Ref<PsiElement> ref) {
        PsiElement location = configurationContext.getPsiLocation();
        if (location instanceof PsiDirectory) {
            PsiDirectory directory = (PsiDirectory) location;
            runConfiguration.getSettings().setTestScope(directory.getVirtualFile().getPresentableUrl());
            runConfiguration.setGeneratedName(runConfiguration.suggestedName());
            ref.set(directory);
            return true;
        }

        if (location instanceof PhpFile) {
            PhpFile phpFile = (PhpFile) location;
            PhpClass testClass = PhpPsiUtil.findClass(phpFile, TesterUtil::isTestClass);
            if (testClass == null) {
                return false;
            }

            runConfiguration.getSettings().setTestScope(phpFile.getVirtualFile().getPresentableUrl());
            runConfiguration.setGeneratedName(runConfiguration.suggestedName());
            ref.set(phpFile);
            return true;
        }

        return false;
    }

    @Override
    public boolean isConfigurationFromContext(TesterRunConfiguration runConfiguration, ConfigurationContext configurationContext) {
        PsiElement location = configurationContext.getPsiLocation();
        if (location instanceof PsiDirectory) {
            PsiDirectory directory = (PsiDirectory) location;
            return runConfiguration.getSettings().getTestScope().equals(directory.getVirtualFile().getPresentableUrl());
        }

        if (location instanceof PhpFile) {
            PhpFile file = (PhpFile) location;
            return runConfiguration.getSettings().getTestScope().equals(file.getVirtualFile().getPresentableUrl());
        }

        return false;
    }
}
