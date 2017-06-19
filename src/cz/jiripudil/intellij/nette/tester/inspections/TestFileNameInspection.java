package cz.jiripudil.intellij.nette.tester.inspections;

import com.intellij.codeInspection.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import cz.jiripudil.intellij.nette.tester.TesterBundle;
import cz.jiripudil.intellij.nette.tester.TesterUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TestFileNameInspection extends LocalInspectionTool {
    private static final Logger LOG = Logger.getInstance("#cz.jiripudil.intellij.nette.tester.inspections.TestFileNameInspection");
    private static final ChangeExtensionToPhptQuickFix CHANGE_EXTENSION_TO_PHPT_QUICK_FIX = new ChangeExtensionToPhptQuickFix();
    private static final AddTestSuffixQuickFix ADD_TEST_SUFFIX_QUICK_FIX = new AddTestSuffixQuickFix();

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PhpElementVisitor() {
            @Override
            public void visitPhpFile(PhpFile phpFile) {
                PhpClass testClass = PhpPsiUtil.findClass(phpFile, TesterUtil::isTestClass);
                if (testClass != null && ! hasValidName(phpFile.getVirtualFile())) {
                    holder.registerProblem(phpFile, TesterBundle.message("inspections.fileName.description"), CHANGE_EXTENSION_TO_PHPT_QUICK_FIX, ADD_TEST_SUFFIX_QUICK_FIX);
                }
            }
        };
    }

    private boolean hasValidName(VirtualFile file) {
        return "phpt".equals(file.getExtension())
            || ("php".equals(file.getExtension()) && StringUtil.endsWith(file.getNameWithoutExtension(), "Test"));
    }

    private static class ChangeExtensionToPhptQuickFix implements LocalQuickFix {
        @Nls
        @NotNull
        @Override
        public String getFamilyName() {
            return TesterBundle.message("inspections.familyName");
        }

        @Nls
        @NotNull
        @Override
        public String getName() {
            return TesterBundle.message("inspections.fileName.quickFix.phpt");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {
            PsiElement element = problemDescriptor.getPsiElement();
            if (element instanceof PhpFile) {
                PhpFile phpFile = (PhpFile) element;
                VirtualFile virtualFile = phpFile.getVirtualFile();
                String newName = virtualFile.getNameWithoutExtension() + ".phpt";

                try {
                    virtualFile.rename(this, newName);

                } catch (IOException e) {
                    LOG.error(e);
                }
            }
        }
    }

    private static class AddTestSuffixQuickFix implements LocalQuickFix {
        @Nls
        @NotNull
        @Override
        public String getFamilyName() {
            return TesterBundle.message("inspections.familyName");
        }

        @Nls
        @NotNull
        @Override
        public String getName() {
            return TesterBundle.message("inspections.fileName.quickFix.test");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {
            PsiElement element = problemDescriptor.getPsiElement();
            if (element instanceof PhpFile) {
                PhpFile phpFile = (PhpFile) element;
                VirtualFile virtualFile = phpFile.getVirtualFile();
                String oldName = virtualFile.getNameWithoutExtension();
                String newName = oldName + "Test";

                PhpClass phpClass = PhpPsiUtil.findClass(phpFile, checkedClass -> checkedClass.getName().equals(oldName));
                if (phpClass != null) {
                    phpClass.setName(newName);
                }

                try {
                    virtualFile.rename(this, newName + "." + virtualFile.getExtension());

                } catch (IOException e) {
                    LOG.error(e);
                }
            }
        }
    }
}
