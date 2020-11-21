package cz.jiripudil.intellij.nette.tester.inspections;

import com.intellij.codeInspection.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.ExtendsList;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.Statement;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import cz.jiripudil.intellij.nette.tester.TesterBundle;
import cz.jiripudil.intellij.nette.tester.TesterUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;

public class TestCaseIsRunInspection extends LocalInspectionTool {
    private static final AddRunMethodCallQuickFix ADD_RUN_METHOD_CALL_QUICK_FIX = new AddRunMethodCallQuickFix();
    private static final MakeAbstractQuickFix MAKE_ABSTRACT_QUICK_FIX = new MakeAbstractQuickFix();

    private ArrayList<PhpClass> testClasses;
    private HashSet<String> runReferencedClassNames;

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PhpElementVisitor() {
            @Override
            public void visitPhpClass(PhpClass phpClass) {
                if (TesterUtil.isTestClass(phpClass)) {
                    testClasses.add(phpClass);
                }
            }

            @Override
            public void visitPhpMethodReference(MethodReference reference) {
                if (reference.getClassReference() == null) {
                    return;
                }

                if (reference.getCanonicalText().equalsIgnoreCase("run") || reference.getCanonicalText().equalsIgnoreCase("runTest")) {
                    runReferencedClassNames.add(reference.getClassReference().getType().toString());
                }
            }
        };
    }

    @Override
    public void inspectionStarted(@NotNull LocalInspectionToolSession session, boolean isOnTheFly) {
        if ( ! (session.getFile() instanceof PhpFile)) {
            return;
        }

        testClasses = new ArrayList<>();
        runReferencedClassNames = new HashSet<>();
    }

    @Override
    public void inspectionFinished(@NotNull LocalInspectionToolSession session, @NotNull ProblemsHolder problemsHolder) {
        if ( ! (session.getFile() instanceof PhpFile)) {
            return;
        }

        testClasses.forEach((PhpClass testClass) -> {
            if ( ! runReferencedClassNames.contains(testClass.getFQN())) {
                ExtendsList extendsList = testClass.getExtendsList();
                TextRange highlightRange = new TextRange(0, extendsList.getStartOffsetInParent() + extendsList.getTextLength());
                problemsHolder.registerProblem(
                    testClass, TesterBundle.message("inspections.runTestCase.description"), ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                    highlightRange, ADD_RUN_METHOD_CALL_QUICK_FIX, ! testClass.isFinal() ? MAKE_ABSTRACT_QUICK_FIX : null
                );
            }
        });
    }

    private static class AddRunMethodCallQuickFix implements LocalQuickFix {
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
            return TesterBundle.message("inspections.runTestCase.addRunMethodCall.quickFix");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {
            PsiElement element = problemDescriptor.getPsiElement();
            if ( ! (element instanceof PhpClass)) {
                return;
            }

            PsiFile containingFile = element.getContainingFile();
            PsiDocumentManager manager = PsiDocumentManager.getInstance(project);
            Document document = manager.getDocument(containingFile);

            PhpClass phpClass = (PhpClass) element;
            String template = "\n\n(new " + phpClass.getName() + "())->run();";
            Statement runMethodCall = PhpPsiElementFactory.createStatement(project, template);

            phpClass.getParent().addAfter(runMethodCall, phpClass);

            if (document != null) {
                manager.doPostponedOperationsAndUnblockDocument(document);
                TextRange reformatRange = runMethodCall.getTextRange();
                CodeStyleManager.getInstance(project).reformatText(containingFile, reformatRange.getStartOffset(), reformatRange.getEndOffset());
            }
        }
    }

    private static class MakeAbstractQuickFix implements LocalQuickFix {
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
            return TesterBundle.message("inspections.runTestCase.makeAbstract.quickFix");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {
            PsiElement element = problemDescriptor.getPsiElement();
            if ( ! (element instanceof PhpClass)) {
                return;
            }

            PsiFile containingFile = element.getContainingFile();
            PsiDocumentManager manager = PsiDocumentManager.getInstance(project);
            Document document = manager.getDocument(containingFile);

            PhpClass phpClass = (PhpClass) element;
            PsiElement abstractKeyword = PhpPsiElementFactory.createFromText(project, LeafPsiElement.class, "abstract");
            if (abstractKeyword == null) {
                return;
            }

            phpClass.addBefore(abstractKeyword, phpClass.getFirstChild());

            if (document != null) {
                manager.doPostponedOperationsAndUnblockDocument(document);
                TextRange reformatRange = abstractKeyword.getTextRange();
                CodeStyleManager.getInstance(project).reformatText(containingFile, reformatRange.getStartOffset(), reformatRange.getEndOffset());
            }
        }
    }
}
