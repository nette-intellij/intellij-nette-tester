package cz.jiripudil.intellij.nette.tester.execution;

import com.intellij.execution.Location;
import com.intellij.execution.PsiLocation;
import com.intellij.execution.testframework.sm.runner.SMTestLocator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.stubs.indexes.PhpMethodIndex;
import com.jetbrains.php.util.pathmapper.PhpPathMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TesterTestLocator implements SMTestLocator {
    private static final String PROTOCOL_FILE = "tester_file";
    private static final String PROTOCOL_METHOD = "tester_method";

    @NotNull
    private final PhpPathMapper pathMapper;

    private TesterTestLocator(@NotNull PhpPathMapper pathMapper) {
        this.pathMapper = pathMapper;
    }

    public static TesterTestLocator create(@NotNull PhpPathMapper pathMapper) {
        return new TesterTestLocator(pathMapper);
    }

    @NotNull
    public PhpPathMapper getPathMapper() {
        return pathMapper;
    }

    @NotNull
    @Override
    public List<Location> getLocation(@NotNull String protocol, @NotNull String path, @NotNull Project project, @NotNull GlobalSearchScope globalSearchScope) {
        if (protocol.equals(PROTOCOL_FILE)) {
            PsiElement element = findFile(path, project);
            if (element != null) {
                return Collections.<Location>singletonList(new PsiLocation(project, element));
            }

        } else if (protocol.equals(PROTOCOL_METHOD)) {
            PsiElement element = findMethod(path, project);
            if (element != null) {
                return Collections.<Location>singletonList(new PsiLocation(project, element));
            }
        }

        return Collections.emptyList();
    }

    @Nullable
    private PsiFile findFile(String path, Project project) {
        VirtualFile virtualFile = this.pathMapper.getLocalFile(path);
        if (virtualFile == null) {
            return null;
        }

        PsiFile result = PsiManager.getInstance(project).findFile(virtualFile);
        if (!(result instanceof PhpFile)) {
            return null;
        }

        return result;
    }

    @Nullable
    private Method findMethod(String path, Project project) {
        String[] location = path.split("#");
        int tokensNumber = location.length;
        if (tokensNumber == 2) {
            String filePath = location[0];
            String methodName = location[1];

            if (filePath == null) {
                return null;

            } else {
                PsiFile file = findFile(filePath, project);
                if (file == null) {
                    return null;
                }

                GlobalSearchScope scope = GlobalSearchScope.fileScope(project, file.getVirtualFile());
                Collection methods = StubIndex.getElements(PhpMethodIndex.KEY, methodName, project, scope, Method.class);
                if (methods.size() == 1) {
                    return (Method) methods.iterator().next();
                }
            }
        }

        return null;
    }
}
