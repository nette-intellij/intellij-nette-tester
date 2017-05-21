package cz.jiripudil.intellij.nette.tester;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.testIntegration.LanguageTestCreators;
import com.jetbrains.php.lang.PhpLanguage;
import cz.jiripudil.intellij.nette.tester.codeGeneration.TesterTestCreator;
import org.jetbrains.annotations.NotNull;

public class TesterTestCreatorEnabler implements ApplicationComponent {
    @Override
    public void initComponent() {
        LanguageTestCreators.INSTANCE.addExplicitExtension(PhpLanguage.INSTANCE, TesterTestCreator.INSTANCE);
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return this.getClass().getName();
    }
}
