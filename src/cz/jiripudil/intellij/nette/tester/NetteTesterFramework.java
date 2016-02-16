package cz.jiripudil.intellij.nette.tester;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.lang.Language;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.testIntegration.TestFramework;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.Processor;
import com.jetbrains.php.PhpClassHierarchyUtils;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.lang.PhpLangUtil;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;


public class NetteTesterFramework implements TestFramework {
    public static final String MIN_SUPPORTED_VERSION = "6.6.6";

    @NotNull
    @Override
    public String getName() {
        return "Nette Tester";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return PhpIcons.PHP_TEST_FILE;
    }

    @Override
    public boolean isLibraryAttached(@NotNull Module module) {
        return false;
    }

    @Nullable
    @Override
    public String getLibraryPath() {
        return null;
    }

    @Nullable
    @Override
    public String getDefaultSuperClass() {
        return "\\Tester\\TestCase";
    }

    @Override
    public boolean isTestClass(@NotNull PsiElement psiElement) {
        if ( ! (psiElement instanceof PhpClass)) {
            return false;
        }

        PhpClass phpClass = (PhpClass) psiElement;
        if (phpClass.isAbstract() || phpClass.isInterface() || phpClass.isTrait()) {
            return false;
        }

        final Ref<Boolean> isTestCase = new Ref<>(false);
        PhpClassHierarchyUtils.processSuperClasses(phpClass, true, true, new Processor<PhpClass>() {
            @Override
            public boolean process(PhpClass phpClass) {
                String superFQN = phpClass.getSuperFQN();
                if (superFQN != null && PhpLangUtil.equalsClassNames(getDefaultSuperClass(), superFQN)) {
                    isTestCase.set(true);
                }

                return !isTestCase.get();
            }
        });

        return isTestCase.get();
    }

    @Override
    public boolean isPotentialTestClass(@NotNull PsiElement psiElement) {
        return isTestClass(psiElement);
    }

    @Nullable
    @Override
    public PsiElement findSetUpMethod(@NotNull PsiElement psiElement) {
        if (!isTestClass(psiElement)) {
            return null;
        }

        PhpClass phpClass = (PhpClass) psiElement;
        final Ref<Method> setUpMethod = new Ref<>(null);
        phpClass.accept(new PhpElementVisitor() {
            @Override
            public void visitPhpMethod(Method method) {
                if (method.getName().toLowerCase().equals("setup")) {
                    setUpMethod.set(method);
                }
            }
        });

        return setUpMethod.get();
    }

    @Nullable
    @Override
    public PsiElement findTearDownMethod(@NotNull PsiElement psiElement) {
        if (!isTestClass(psiElement)) {
            return null;
        }

        PhpClass phpClass = (PhpClass) psiElement;
        final Ref<Method> tearDownMethod = new Ref<>(null);
        phpClass.accept(new PhpElementVisitor() {
            @Override
            public void visitPhpMethod(Method method) {
                if (method.getName().toLowerCase().equals("teardown")) {
                    tearDownMethod.set(method);
                }
            }
        });

        return tearDownMethod.get();
    }

    @Nullable
    @Override
    public PsiElement findOrCreateSetUpMethod(@NotNull PsiElement psiElement) throws IncorrectOperationException {
        return null;
    }

    @Override
    public FileTemplateDescriptor getSetUpMethodFileTemplateDescriptor() {
        return null;
    }

    @Override
    public FileTemplateDescriptor getTearDownMethodFileTemplateDescriptor() {
        return null;
    }

    @Override
    public FileTemplateDescriptor getTestMethodFileTemplateDescriptor() {
        return null;
    }

    @Override
    public boolean isIgnoredMethod(PsiElement psiElement) {
        if ( ! (psiElement instanceof Method)) {
            return false;
        }

        Method method = (Method) psiElement;
        return ! isTestMethod(method)
            || (method.getDocComment() != null && method.getDocComment().getTagElementsByName("skip").length > 0);
    }

    @Override
    public boolean isTestMethod(PsiElement psiElement) {
        if ( ! (psiElement instanceof Method)) {
            return false;
        }

        Method method = (Method) psiElement;
        return method.getName().startsWith("test");
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return PhpLanguage.INSTANCE;
    }
}
