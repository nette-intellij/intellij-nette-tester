package cz.jiripudil.intellij.nette.tester.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.completion.insert.PhpSymbolInsertHandler;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocPsiElement;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import cz.jiripudil.intellij.nette.tester.TesterUtil;
import org.jetbrains.annotations.NotNull;

public class PhpDocCompletionContributor extends CompletionContributor {
    public PhpDocCompletionContributor() {
        this.extend(CompletionType.BASIC, PlatformPatterns.psiElement().withParent(PhpDocPsiElement.class), new DocTagsCompletionProvider());
    }

    private static class DocTagsCompletionProvider extends CompletionProvider<CompletionParameters> {
        private static final String[] fileTags = new String[]{
                "@phpVersion", "@dataProvider", "@multiple", "@exitCode", "@httpCode",
                "@outputMatch", "@outputMatchFile", "@phpIni", "@phpExtension"
        };
        private static final String[] fileTagsNoArgument = new String[]{"@testCase", "@skip"};
        private static final String[] methodTags = new String[]{"@dataProvider"};

        @Override
        protected void addCompletions(
            @NotNull CompletionParameters parameters,
            @NotNull ProcessingContext context,
            @NotNull CompletionResultSet result
        ) {
            if (!TesterUtil.hasValidFileName(parameters.getOriginalFile().getVirtualFile())) {
                return;
            }

            PsiElement currentElement = parameters.getPosition().getOriginalElement();
            PhpDocComment docCommentAtCaret = PhpPsiUtil.getParentByCondition(currentElement, PhpDocComment.INSTANCEOF);
            if (docCommentAtCaret == null) {
                return;
            }

            boolean atTagName = currentElement.getText().startsWith("@");
            if (isDocCommentRedByTester(docCommentAtCaret)) {
                for (String tag : fileTags) {
                    result.addElement(createDocTagLookup(tag, atTagName, true));
                }
                for (String tag : fileTagsNoArgument) {
                    result.addElement(createDocTagLookup(tag, atTagName, false));
                }
            } else {
                PhpPsiElement next = docCommentAtCaret.getNextPsiSibling();
                if (next instanceof Method && TesterUtil.isTestMethod((Method) next)) {
                    for (String tag : methodTags) {
                        result.addElement(createDocTagLookup(tag, atTagName, true));
                    }
                }
            }
        }

        private static boolean isDocCommentRedByTester(PhpDocComment docComment) {
            return docComment.equals(TesterUtil.findDocCommentRedByTester(docComment.getContainingFile()));
        }

        private static LookupElementBuilder createDocTagLookup(String tagName, boolean atTagName, boolean withArgument) {
            String lookupString = atTagName ? tagName.substring(1) : tagName;
            LookupElementBuilder builder = LookupElementBuilder.create(lookupString).withBoldness(true);
            return withArgument ? builder.withInsertHandler(new PhpSymbolInsertHandler(' ', true)) : builder;
        }
    }

}