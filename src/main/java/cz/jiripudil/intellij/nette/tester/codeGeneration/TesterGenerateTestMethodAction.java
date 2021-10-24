package cz.jiripudil.intellij.nette.tester.codeGeneration;

import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.ConstantNode;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.config.PhpLanguageFeature;
import com.jetbrains.php.config.PhpLanguageLevel;
import com.jetbrains.php.config.PhpProjectConfigurationFacade;
import cz.jiripudil.intellij.nette.tester.TesterBundle;
import org.jetbrains.annotations.NotNull;

public class TesterGenerateTestMethodAction extends TesterAbstractGenerateMethodAction {
    public TesterGenerateTestMethodAction() {
        getTemplatePresentation().setText(TesterBundle.message("action.generateTestMethod.name"));
        getTemplatePresentation().setDescription(TesterBundle.message("action.generateTestMethod.description"));
    }

    @Override
    protected void insertMethod(@NotNull Project project, @NotNull Editor editor) {
        Template template = TemplateManager.getInstance(project).createTemplate("", "");
        template.addTextSegment("public function test");

        Expression nameExpr = new ConstantNode("");
        template.addVariable("name", nameExpr, nameExpr, true);
        template.addTextSegment("()");

        PhpLanguageLevel languageLevel = PhpProjectConfigurationFacade.getInstance(project).getLanguageLevel();
        if (languageLevel.hasFeature(PhpLanguageFeature.RETURN_VOID)) {
            template.addTextSegment(": void");
        }

        template.addTextSegment("\n{\n");
        template.addEndVariable();
        template.addTextSegment("\n}");
        template.setToIndent(true);
        template.setToReformat(true);
        template.setToShortenLongNames(true);
        TemplateManager.getInstance(project).startTemplate(editor, template, null);
    }
}
