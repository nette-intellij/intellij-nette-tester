package cz.jiripudil.intellij.nette.tester;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testIntegration.TestFinder;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class TesterTestFinder implements TestFinder {
    @Nullable
    @Override
    public PsiElement findSourceElement(@NotNull PsiElement psiElement) {
        return PsiTreeUtil.getParentOfType(psiElement, PhpNamedElement.class, false);
    }

    @NotNull
    @Override
    public Collection<PsiElement> findTestsForClass(@NotNull PsiElement psiElement) {
        Collection<PsiElement> tests = new ArrayList<>();

        PhpClass phpClass = PsiTreeUtil.getStubOrPsiParentOfType(psiElement, PhpClass.class);
        if (phpClass != null) {
            Project project = phpClass.getProject();
            Collection<PhpClass> testClasses = PhpIndex.getInstance(project).getClassesByName(phpClass.getName() + "Test");
            if (!testClasses.isEmpty()) {
                tests.addAll(testClasses);
            }

            PsiFile[] files = FilenameIndex.getFilesByName(project, phpClass.getName() + ".phpt", GlobalSearchScope.projectScope(project));
            tests.addAll(Arrays.asList(files));
        }

        return tests;
    }

    @NotNull
    @Override
    public Collection<PsiElement> findClassesForTest(@NotNull PsiElement psiElement) {
        Collection<PsiElement> classes = new ArrayList<>();

        PhpClass testClass = PsiTreeUtil.getStubOrPsiParentOfType(psiElement, PhpClass.class);
        if (testClass != null && StringUtil.endsWith(testClass.getName(), "Test")) {
            Project project = testClass.getProject();
            Collection<PhpClass> sourceClasses = PhpIndex.getInstance(project).getClassesByName(testClass.getName().substring(0, testClass.getName().length() - "Test".length()));
            if (!sourceClasses.isEmpty()) {
                classes.addAll(sourceClasses);
            }
        }

        PsiFile containingFile = psiElement.getContainingFile();
        if (containingFile != null) {
            Project project = containingFile.getProject();
            String name = containingFile.getVirtualFile().getNameWithoutExtension();
            if (StringUtil.endsWith(name, "Test")) {
                name = name.substring(0, name.length() - "Test".length());
            }

            Collection<PhpClass> sourceClasses = PhpIndex.getInstance(project).getClassesByName(name);
            classes.addAll(sourceClasses);
        }

        return classes;
    }

    @Override
    public boolean isTest(@NotNull PsiElement psiElement) {
        PsiFile containingFile = psiElement.getContainingFile();
        return containingFile instanceof PhpPsiElement && (
            (containingFile.getVirtualFile() != null
                && containingFile.getVirtualFile().getExtension() != null
                && containingFile.getVirtualFile().getExtension().equals("phpt"))
            || StringUtil.endsWith(containingFile.getName(), "Test")
        );
    }
}
