package cz.jiripudil.intellij.nette.tester.execution;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.jetbrains.php.run.filters.PhpFilter;
import com.jetbrains.php.util.pathmapper.PhpPathMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TesterStackTraceFilter extends PhpFilter {
    private static final Logger LOG = Logger.getInstance("#cz.jiripudil.intellij.nette.tester.execution.TesterStackTraceFilter");
    private static final Pattern STACK_TRACE_PATTERN;

    TesterStackTraceFilter(@NotNull Project project, @NotNull PhpPathMapper pathMapper) {
        super(project, pathMapper);
    }

    @Nullable
    @Override
    public MyResult applyFilter(@NotNull String line) {
        return apply(STACK_TRACE_PATTERN, line);
    }

    private MyResult apply(Pattern pattern, String line) {
        try {
            Matcher matcher = pattern.matcher(StringUtil.newBombedCharSequence(line, 1000L));
            if (matcher.find()) {
                String fileName = matcher.group(1);
                String lineNumberString = matcher.group(2);
                if (fileName == null || lineNumberString == null) {
                    return null;
                }

                try {
                    int lineNumber = Integer.parseInt(lineNumberString);
                    return new MyResult(fileName, lineNumber, matcher.start(1), matcher.end(1));
                } catch (NumberFormatException var6) {
                    // ignore
                }
            }

            return null;
        } catch (ProcessCanceledException var7) {
            LOG.warn("Matching took too long for line: " + line);
            return null;
        }
    }

    static {
        STACK_TRACE_PATTERN = Pattern.compile("in\\s(" + WHOLE_FILENAME_PATTERN + ")\\((\\p{Digit}*)\\)");
    }
}
