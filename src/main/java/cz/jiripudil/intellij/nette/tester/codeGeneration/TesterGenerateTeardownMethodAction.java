package cz.jiripudil.intellij.nette.tester.codeGeneration;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import cz.jiripudil.intellij.nette.tester.TesterBundle;
import org.jetbrains.annotations.NotNull;

public class TesterGenerateTeardownMethodAction extends TesterAbstractGenerateMethodAction {
    public TesterGenerateTeardownMethodAction() {
        getTemplatePresentation().setText(TesterBundle.message("action.generateTeardownMethod.name"));
        getTemplatePresentation().setDescription(TesterBundle.message("action.generateTeardownMethod.description"));
    }

    @Override
    protected void insertMethod(@NotNull Project project, @NotNull Editor editor) {
        Template template = TemplateManager.getInstance(project).createTemplate("", "");
        template.addTextSegment("protected function tearDown()\n{\n");
        template.addTextSegment("parent::tearDown();\n");
        template.addEndVariable();
        template.addTextSegment("\n}");
        template.setToIndent(true);
        template.setToReformat(true);
        template.setToShortenLongNames(true);
        TemplateManager.getInstance(project).startTemplate(editor, template, null);
    }
}
