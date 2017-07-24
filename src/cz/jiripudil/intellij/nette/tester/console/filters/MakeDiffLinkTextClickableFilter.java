package cz.jiripudil.intellij.nette.tester.console.filters;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.testframework.stacktrace.DiffHyperlink;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import cz.jiripudil.intellij.nette.tester.console.ActualExpectedPathDetector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * Catches text {@link InsertDiffLinkTextInputFilter#DIFF_LINK_TEXT} and makes
 * it clickable if previous line contains diff shell command (see: {@link ActualExpectedPathDetector#DIFF_LINE_REGEX}).
 */
public class MakeDiffLinkTextClickableFilter implements Filter {
    private String expectedPath;
    private String actualPath;

    @Nullable
    @Override
    public Result applyFilter(String line, int endPoint) {
        if (line.equals(InsertDiffLinkTextInputFilter.DIFF_LINK_TEXT + "\n")) {
            if (expectedPath != null && actualPath != null) {
                Result result = new Result(endPoint - line.length(), endPoint, new LazyDiffHyperlinkInfo(expectedPath, actualPath));
                expectedPath = actualPath = null;
                return result;
            }
            return null;
        }

        Pair<String, String> paths = ActualExpectedPathDetector.detectPaths(line);
        if (paths != null) {
            expectedPath = paths.first;
            actualPath = paths.second;
        }

        return null;
    }

    private static class LazyDiffHyperlinkInfo implements HyperlinkInfo {
        private String expectedPath;
        private String actualPath;
        private DiffHyperlink.DiffHyperlinkInfo link;

        LazyDiffHyperlinkInfo(@NotNull String expectedPath, @NotNull String actualPath) {
            this.expectedPath = expectedPath;
            this.actualPath = actualPath;
        }

        @Override
        public void navigate(Project project) {
            if (link == null) {
                String expected, actual;
                try {
                    expected = FileUtil.loadFile(new File(expectedPath));
                    actual = FileUtil.loadFile(new File(actualPath));
                } catch (IOException e) {
                    return;
                }
                link = new DiffHyperlink(expected, actual, expectedPath, actualPath, false).new DiffHyperlinkInfo();
            }

            link.navigate(project);
        }
    }
}
