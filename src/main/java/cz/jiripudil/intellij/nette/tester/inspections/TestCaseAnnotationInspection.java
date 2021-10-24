package cz.jiripudil.intellij.nette.tester.inspections;

import com.intellij.codeInspection.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.documentation.phpdoc.lexer.PhpDocTokenTypes;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import cz.jiripudil.intellij.nette.tester.TesterBundle;
import cz.jiripudil.intellij.nette.tester.TesterUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class TestCaseAnnotationInspection extends LocalInspectionTool {
    private static final AnnotateTestCaseQuickFix QUICK_FIX = new AnnotateTestCaseQuickFix();

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PhpElementVisitor() {
            @Override
            public void visitPhpClass(PhpClass phpClass) {
            if (!TesterUtil.isTestClass(phpClass)) {
                return;
            }

            PhpDocComment docComment = TesterUtil.findDocCommentRedByTester(phpClass.getContainingFile());
            if (phpClass.getIdentifyingElement() != null && (docComment == null || docComment.getTagElementsByName("@testCase").length == 0)) {
                holder.registerProblem(
                    phpClass.getNameIdentifier() != null ? phpClass.getNameIdentifier() : phpClass,
                    TesterBundle.message("inspections.annotation.description"),
                    QUICK_FIX
                );
            }
            }
        };
    }

    private static class AnnotateTestCaseQuickFix implements LocalQuickFix {
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
            return TesterBundle.message("inspections.annotation.quickFix");
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {
            PhpClass phpClass = (PhpClass) problemDescriptor.getPsiElement();
            PsiFile containingFile = phpClass.getContainingFile();

            PsiDocumentManager manager = PsiDocumentManager.getInstance(project);
            Document document = manager.getDocument(containingFile);

            String template = "/**\n* @testCase\n*/";
            PsiElement testCaseTag = PhpPsiElementFactory.createFromText(project, PhpDocTag.class, template);
            assert testCaseTag != null;

            PhpDocComment docComment = TesterUtil.findDocCommentRedByTester(phpClass.getContainingFile());
            if (docComment == null) {
                PsiElement testCaseDocComment = PhpPsiElementFactory.createFromText(project, PhpDocComment.class, template);
                assert testCaseDocComment != null;
                phpClass.getParent().addBefore(testCaseDocComment, phpClass);

                if (document != null) {
                    manager.doPostponedOperationsAndUnblockDocument(document);
                    TextRange reformatRange = testCaseDocComment.getTextRange();
                    CodeStyleManager.getInstance(project).reformatText(containingFile, reformatRange.getStartOffset(), reformatRange.getEndOffset());
                }

            } else {
                PsiElement star = PhpPsiElementFactory.createFromText(project, PhpDocTokenTypes.DOC_LEADING_ASTERISK, template);
                PsiElement lineFeed = star.getPrevSibling();
                PsiElement insertAfter = PhpPsiUtil.getChildOfType(docComment, PhpDocTokenTypes.DOC_COMMENT_END);

                if (insertAfter != null) {
                    insertAfter = insertAfter.getPrevSibling();
                    int offset = insertAfter.getTextRange().getStartOffset();
                    PsiElement insertedLF = null;
                    if (insertAfter instanceof PsiWhiteSpace && !insertAfter.textContains('\n') && lineFeed != null) {
                        insertedLF = docComment.addAfter(lineFeed, insertAfter);
                    }

                    testCaseTag = docComment.addAfter(testCaseTag, insertedLF == null ? insertAfter : insertedLF);
                    docComment.addBefore(star, testCaseTag);
                    if (document != null) {
                        manager.doPostponedOperationsAndUnblockDocument(document);
                        PhpDocComment updatedComment = PsiTreeUtil.getParentOfType(containingFile.findElementAt(offset), PhpDocComment.class);
                        if (updatedComment != null) {
                            TextRange reformatRange = updatedComment.getTextRange();
                            CodeStyleManager.getInstance(project).reformatText(containingFile, reformatRange.getStartOffset(), reformatRange.getEndOffset());
                        }
                    }
                }
            }
        }
    }
}
