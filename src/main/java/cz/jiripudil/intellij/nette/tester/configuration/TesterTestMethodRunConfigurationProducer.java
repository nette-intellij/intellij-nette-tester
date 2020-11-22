package cz.jiripudil.intellij.nette.tester.configuration;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.ide.scratch.ScratchFileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
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
import org.jetbrains.annotations.Nullable;

public class TesterTestMethodRunConfigurationProducer extends RunConfigurationProducer<TesterTestMethodRunConfiguration> {
    protected TesterTestMethodRunConfigurationProducer() {
        super(TesterTestMethodRunConfigurationType.getInstance());
    }

    @Override
    protected boolean setupConfigurationFromContext(TesterTestMethodRunConfiguration runConfiguration, ConfigurationContext context, Ref<PsiElement> ref) {
        PsiElement element = context.getPsiLocation();
        Method method = PhpPsiUtil.getParentByCondition(element, parent -> parent instanceof Method);

        if (method != null && isValid(method)) {
            VirtualFile file = method.getContainingFile().getVirtualFile();
            ref.set(method);

            if (!FileTypeManager.getInstance().isFileOfType(file, ScratchFileType.INSTANCE)) {
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
    public boolean isConfigurationFromContext(TesterTestMethodRunConfiguration runConfiguration, ConfigurationContext context) {
        PsiElement element = context.getPsiLocation();
        Method method = PhpPsiUtil.getParentByCondition(element, parent -> parent instanceof Method);

        if (method != null && isValid(method)) {
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
}
