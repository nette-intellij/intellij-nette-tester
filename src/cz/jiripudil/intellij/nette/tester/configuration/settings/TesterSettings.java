package cz.jiripudil.intellij.nette.tester.configuration.settings;

import com.jetbrains.php.run.PhpCommandLineSettings;
import org.jetbrains.annotations.NotNull;

public class TesterSettings implements Cloneable {
    public String testScope;
    public String testerExecutable;
    public String testerOptions;
    public String phpIniPath;

    private PhpCommandLineSettings phpCommandLineSettings = new PhpCommandLineSettings();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TesterSettings)) return false;

        TesterSettings that = (TesterSettings) o;

        if (testScope != null ? !testScope.equals(that.testScope) : that.testScope != null) return false;
        if (testerExecutable != null ? !testerExecutable.equals(that.testerExecutable) : that.testerExecutable != null)
            return false;
        if (testerOptions != null ? !testerOptions.equals(that.testerOptions) : that.testerOptions != null)
            return false;
        if (phpIniPath != null ? !phpIniPath.equals(that.phpIniPath) : that.phpIniPath != null) return false;
        return phpCommandLineSettings.equals(that.getPhpCommandLineSettings());
    }

    @Override
    public int hashCode() {
        int result = testScope != null ? testScope.hashCode() : 0;
        result = 31 * result + (testerExecutable != null ? testerExecutable.hashCode() : 0);
        result = 31 * result + (testerOptions != null ? testerOptions.hashCode() : 0);
        result = 31 * result + (phpIniPath != null ? phpIniPath.hashCode() : 0);
        result = 31 * result + phpCommandLineSettings.hashCode();
        return result;
    }

    @NotNull
    public PhpCommandLineSettings getPhpCommandLineSettings() {
        return phpCommandLineSettings;
    }

    @Override
    public TesterSettings clone() throws CloneNotSupportedException {
        return (TesterSettings) super.clone();
    }
}
