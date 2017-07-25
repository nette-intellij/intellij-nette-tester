package cz.jiripudil.intellij.nette.tester.console.filters;

import com.intellij.execution.ExecutionBundle;
import com.intellij.execution.filters.InputFilter;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.util.Pair;
import cz.jiripudil.intellij.nette.tester.console.ActualExpectedPathDetector;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Adds text {@link InsertDiffLinkTextInputFilter#DIFF_LINK_TEXT} after a line with
 * diff shell command (see: {@link ActualExpectedPathDetector#DIFF_LINE_REGEX}).
 */
public class InsertDiffLinkTextInputFilter implements InputFilter {
    final static String DIFF_LINK_TEXT = ExecutionBundle.message("junit.click.to.see.diff.link");

    @Nullable
    @Override
    public List<Pair<String, ConsoleViewContentType>> applyFilter(String text, ConsoleViewContentType contentType) {
        if (ActualExpectedPathDetector.detectPaths(text) != null) {
            return Collections.singletonList(Pair.create(text + DIFF_LINK_TEXT + "\n", contentType));
        }
        return null;
    }
}
