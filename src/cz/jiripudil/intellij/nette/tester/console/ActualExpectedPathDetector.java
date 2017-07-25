package cz.jiripudil.intellij.nette.tester.console;

import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ActualExpectedPathDetector {
    /**
     * @see <a href="https://github.com/nette/tester/blob/c126385e/src/Framework/Dumper.php#L307">diff line</a>
     * @see <a href="https://github.com/nette/tester/blob/c126385e/src/Framework/Helpers.php#L82">argument escaping</a>
     */
    private final static Pattern DIFF_LINE_REGEX;

    static {
        String unixArg = "'(?:[^']|'\\\\'')+'";
        String winArg = "\"[^\"]+\"";
        String unquotedArg = "[a-z0-9._=/:-]+";
        String arg = "(" + unixArg + "|" + winArg + "|" + unquotedArg + ")";
        DIFF_LINE_REGEX = Pattern.compile("^diff " + arg + " " + arg + "$");
    }

    @Nullable
    public static Pair<String, String> detectPaths(final String diffLine) {
        Matcher matcher = DIFF_LINE_REGEX.matcher(diffLine);
        if (matcher.find()) {
            return Pair.create(unquoteArg(matcher.group(1)), unquoteArg(matcher.group(2)));
        }
        return null;
    }

    private static String unquoteArg(String arg) {
        if (arg.startsWith("'")) {
            return StringUtil.unquoteString(arg).replace("'\\''", "'");
        }

        if (arg.startsWith("\"")) {
            return StringUtil.unquoteString(arg);
        }

        return arg;
    }
}
