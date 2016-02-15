package cz.jiripudil.intellij.nette.tester.version;

import com.jetbrains.php.PhpTestFrameworkVersionDetector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TesterVersionDetector extends PhpTestFrameworkVersionDetector<TesterVersion> {
    public static final TesterVersionDetector INSTANCE = new TesterVersionDetector();

    @NotNull
    @Override
    protected String getTitle() {
        return "Detecting Nette Tester version";
    }

    @Nullable
    @Override
    protected TesterVersion parse(@NotNull String s) {
        Pattern pattern = Pattern.compile("v((\\d+)\\.?)*");
        Matcher matcher = pattern.matcher(s);

        if (matcher.find()) {
            return new TesterVersion(matcher.group().substring(1));

        } else {
            return new TesterUnknownVersion();
        }
    }

    @NotNull
    @Override
    protected String getVersionOption() {
        return "--help";
    }
}
