package cz.jiripudil.intellij.nette.tester;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.testIntegration.LanguageTestCreators;
import com.jetbrains.php.lang.PhpLanguage;
import cz.jiripudil.intellij.nette.tester.codeGeneration.TesterTestCreator;
import org.jetbrains.annotations.NotNull;

public class TesterPostStartupActivity implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        LanguageTestCreators.INSTANCE.addExplicitExtension(PhpLanguage.INSTANCE, TesterTestCreator.INSTANCE);
    }
}
