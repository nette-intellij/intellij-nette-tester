package cz.jiripudil.intellij.nette.tester.codeGeneration;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class TesterGenerateSetupMethodAction extends TesterAbstractGenerateMethodAction {
    public TesterGenerateSetupMethodAction() {
        getTemplatePresentation().setText("TestCase setUp() method");
        getTemplatePresentation().setDescription("Creates a Nette Tester setUp() method");
    }

    @Override
    protected void insertMethod(@NotNull Project project, @NotNull Editor editor) {
        Template template = TemplateManager.getInstance(project).createTemplate("", "");
        template.addTextSegment("protected function setUp()\n{\n");
        template.addTextSegment("parent::setUp();\n");
        template.addEndVariable();
        template.addTextSegment("\n}");
        template.setToIndent(true);
        template.setToReformat(true);
        template.setToShortenLongNames(true);
        TemplateManager.getInstance(project).startTemplate(editor, template, null);
    }
}
