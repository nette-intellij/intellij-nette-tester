package cz.jiripudil.intellij.nette.tester;

import com.intellij.openapi.util.Ref;
import com.jetbrains.php.PhpClassHierarchyUtils;
import com.jetbrains.php.lang.PhpLangUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

public class TesterUtil {
    public static boolean isTestClass(@NotNull PhpClass phpClass) {
        if (phpClass.isAbstract() || phpClass.isInterface() || phpClass.isTrait()) {
            return false;
        }

        final Ref<Boolean> isTestCase = new Ref<>(false);
        PhpClassHierarchyUtils.processSuperClasses(phpClass, true, true, phpClass1 -> {
            String superFQN = phpClass1.getSuperFQN();
            if (superFQN != null && PhpLangUtil.equalsClassNames("\\Tester\\TestCase", superFQN)) {
                isTestCase.set(true);
            }

            return !isTestCase.get();
        });

        return isTestCase.get();
    }
}
