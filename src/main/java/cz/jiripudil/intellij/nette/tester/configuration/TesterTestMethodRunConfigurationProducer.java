package cz.jiripudil.intellij.nette.tester.configuration;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.LazyRunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.ide.scratch.ScratchUtil;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.run.script.PhpScriptRunConfiguration;
import cz.jiripudil.intellij.nette.tester.TesterUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TesterTestMethodRunConfigurationProducer extends LazyRunConfigurationProducer<TesterTestMethodRunConfiguration> {
    protected TesterTestMethodRunConfigurationProducer() {
        super();
    }

    @Override
    protected boolean setupConfigurationFromContext(
            @NotNull TesterTestMethodRunConfiguration runConfiguration,
            ConfigurationContext context,
            @NotNull Ref<PsiElement> ref
    ) {
        PsiElement element = context.getPsiLocation();
        Method method = PhpPsiUtil.getParentByCondition(element, parent -> parent instanceof Method);

        if (isValid(method)) {
            VirtualFile file = method.getContainingFile().getVirtualFile();
            ref.set(method);

            if (!ScratchUtil.isScratch(file)) {
                VirtualFile root = ProjectRootManager.getInstance(element.getProject()).getFileIndex().getContentRootForFile(file);
                if (root == null) {
                    return false;
                }
            }

            PhpScriptRunConfiguration.Settings settings = runConfiguration.getSettings();
            settings.setPath(file.getPresentableUrl());
            runConfiguration.setMethod(method);
            runConfiguration.setName(runConfiguration.suggestedName());
            return true;
        }

        return false;
    }

    @Override
    public boolean isConfigurationFromContext(@NotNull TesterTestMethodRunConfiguration runConfiguration, ConfigurationContext context) {
        PsiElement element = context.getPsiLocation();
        Method method = PhpPsiUtil.getParentByCondition(element, parent -> parent instanceof Method);

        if (isValid(method)) {
            VirtualFile containingVirtualFile = method.getContainingFile().getVirtualFile();
            PhpScriptRunConfiguration.Settings settings = runConfiguration.getSettings();
            String path = settings.getPath();

            if (path != null) {
                VirtualFile configurationFile = LocalFileSystem.getInstance().findFileByPath(path);
                if (configurationFile != null) {
                    return StringUtil.equals(containingVirtualFile.getPath(), configurationFile.getPath())
                        && runConfiguration.isMethod(method);
                }
            }
        }

        return false;
    }

    private boolean isValid(@Nullable Method method) {
        return method != null
            && isValid(method.getContainingFile())
            && TesterUtil.isTestMethod(method);
    }

    private boolean isValid(@Nullable PsiFile containingFile) {
        return containingFile != null
            && containingFile.getFileType() == PhpFileType.INSTANCE
            && containingFile.getVirtualFile() != null;
    }

    @NotNull
    @Override
    public ConfigurationFactory getConfigurationFactory() {
        return TesterTestMethodRunConfigurationType.createFactory();
    }
}
