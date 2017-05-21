package cz.jiripudil.intellij.nette.tester.codeGeneration;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import cz.jiripudil.intellij.nette.tester.projectSettings.TesterNamespaceMapping;
import cz.jiripudil.intellij.nette.tester.projectSettings.TesterProjectSettings;
import cz.jiripudil.intellij.nette.tester.projectSettings.TesterProjectSettingsManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TesterNamespaceMapper {
    @NotNull private final Project project;

    private TesterNamespaceMapper(@NotNull final Project project) {
        this.project = project;
    }

    public static TesterNamespaceMapper getInstance(@NotNull final Project project) {
        return new TesterNamespaceMapper(project);
    }

    @NotNull
    String mapSourceNamespaceToTestNamespace(@NotNull PhpClass sourceClass) {
        TesterProjectSettings settings = TesterProjectSettingsManager.getInstance(project).getState();
        String namespaceName = trimNamespaceSeparators(sourceClass.getNamespaceName());
        if (settings == null) {
            return namespaceName;
        }

        List<TesterNamespaceMapping> mappings = settings.getNamespaceMappings();
        for (TesterNamespaceMapping mapping : mappings) {
            String fqn = trimNamespaceSeparators(sourceClass.getFQN());
            if (StringUtil.startsWith(fqn, mapping.getSourceNamespace() + "\\")) {
                return trimNamespaceSeparators(
                    mapping.getTestsNamespace()
                        + "\\" +
                        trimNamespaceSeparators(namespaceName.substring(mapping.getSourceNamespace().length()))
                );
            }
        }

        return namespaceName;
    }

    @NotNull
    private String trimNamespaceSeparators(@NotNull String namespace) {
        return StringUtil.trimStart(StringUtil.trimEnd(namespace, "\\"), "\\");
    }
}
