package cz.jiripudil.intellij.nette.tester.execution;

import com.intellij.execution.Location;
import com.intellij.execution.PsiLocation;
import com.intellij.execution.testframework.sm.runner.SMTestLocator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.jetbrains.php.config.PhpProjectConfigurationFacade;
import com.jetbrains.php.config.servers.PhpServer;
import com.jetbrains.php.util.pathmapper.PhpPathMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class TesterLocationProvider implements SMTestLocator {
    private static final String PROTOCOL_ID = "php_qn";

    @NotNull
    private final PhpPathMapper pathMapper;

    private TesterLocationProvider(@NotNull PhpPathMapper pathMapper) {
        this.pathMapper = pathMapper;
    }

    public static TesterLocationProvider create(@NotNull Project project, @NotNull String serverName) {
        PhpServer server = PhpProjectConfigurationFacade.getInstance(project).findServer(serverName);
        return server == null ? new TesterLocationProvider(PhpPathMapper.create()) : new TesterLocationProvider(PhpPathMapper.create(server));
    }

    public static TesterLocationProvider create(@NotNull PhpPathMapper pathMapper) {
        return new TesterLocationProvider(pathMapper);
    }

    @NotNull
    public PhpPathMapper getPathMapper() {
        return pathMapper;
    }

    @NotNull
    @Override
    public List<Location> getLocation(@NotNull String protocol, @NotNull String path, @NotNull Project project, @NotNull GlobalSearchScope globalSearchScope) {
        if (PROTOCOL_ID.equals(protocol)) {
            PsiElement element = this.findElement(path, project);
            if (element != null) {
                return Collections.<Location>singletonList(new PsiLocation(project, element));
            }
        }

        return Collections.emptyList();
    }

    @Nullable
    private PsiElement findElement(String path, Project project) {
        return null;
    }
}
