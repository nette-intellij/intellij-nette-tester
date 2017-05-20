package cz.jiripudil.intellij.nette.tester.configuration;

import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.annotations.Property;
import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.php.config.interpreters.PhpConfigurationOptionData;
import com.jetbrains.php.config.interpreters.PhpInterpreter;
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl;
import com.jetbrains.php.run.PhpCommandLineSettings;
import com.jetbrains.php.run.PhpRunConfigurationSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TesterSettings implements PhpRunConfigurationSettings, Cloneable {
    @Nullable private String testScope;
    @Nullable private String testerExecutable;
    @Nullable private String testerOptions;
    @Nullable private String setupScriptPath;

    @Nullable private String phpInterpreterId;
    @NotNull private List<PhpConfigurationOptionData> phpInterpreterOptions = new ArrayList<>();
    @Nullable private String phpIniPath;
    @NotNull private Boolean useSystemPhpIni = Boolean.FALSE;

    @NotNull private PhpCommandLineSettings phpCommandLineSettings = new PhpCommandLineSettings();

    @Property
    @Nullable
    public String getTestScope() {
        return testScope;
    }

    public void setTestScope(@Nullable String testScope) {
        this.testScope = testScope;
    }

    @Property
    @Nullable
    public String getTesterExecutable() {
        return testerExecutable;
    }

    public void setTesterExecutable(@Nullable String testerExecutable) {
        this.testerExecutable = testerExecutable;
    }

    @Property
    @Nullable
    public String getTesterOptions() {
        return testerOptions;
    }

    public void setTesterOptions(@Nullable String testerOptions) {
        this.testerOptions = testerOptions;
    }

    @Property
    @Nullable
    public String getSetupScriptPath() {
        return setupScriptPath;
    }

    public void setSetupScriptPath(@Nullable String setupScriptPath) {
        this.setupScriptPath = setupScriptPath;
    }

    @Property
    @Nullable
    public String getPhpInterpreterId() {
        return phpInterpreterId;
    }

    @Transient
    @Nullable
    public PhpInterpreter getPhpInterpreter(@NotNull Project project) {
        return PhpInterpretersManagerImpl.getInstance(project).findInterpreterById(phpInterpreterId);
    }

    public void setPhpInterpreterId(@Nullable String phpInterpreterId) {
        this.phpInterpreterId = phpInterpreterId;
    }

    @Property
    @NotNull
    public List<PhpConfigurationOptionData> getPhpInterpreterOptions() {
        return phpInterpreterOptions;
    }

    public void setPhpInterpreterOptions(@NotNull List<PhpConfigurationOptionData> phpInterpreterOptions) {
        this.phpInterpreterOptions = phpInterpreterOptions;
    }

    @Property
    @Nullable
    public String getPhpIniPath() {
        return phpIniPath;
    }

    public void setPhpIniPath(@Nullable String phpIniPath) {
        this.phpIniPath = phpIniPath;
    }

    @Property
    @NotNull
    public Boolean getUseSystemPhpIni() {
        return useSystemPhpIni;
    }

    public void setUseSystemPhpIni(@NotNull Boolean useSystemPhpIni) {
        this.useSystemPhpIni = useSystemPhpIni;
    }

    @Property
    @NotNull
    public PhpCommandLineSettings getPhpCommandLineSettings() {
        return phpCommandLineSettings;
    }

    @Nullable
    @Override
    public String getWorkingDirectory() {
        return phpCommandLineSettings.getWorkingDirectory();
    }

    @Override
    public void setWorkingDirectory(@NotNull String workingDirectory) {
        phpCommandLineSettings.setWorkingDirectory(workingDirectory);
    }

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
        if (setupScriptPath != null ? !setupScriptPath.equals(that.setupScriptPath) : that.setupScriptPath != null) return false;

        return phpCommandLineSettings.equals(that.getPhpCommandLineSettings());
    }

    @Override
    public int hashCode() {
        int result = testScope != null ? testScope.hashCode() : 0;
        result = 31 * result + (testerExecutable != null ? testerExecutable.hashCode() : 0);
        result = 31 * result + (testerOptions != null ? testerOptions.hashCode() : 0);
        result = 31 * result + (phpIniPath != null ? phpIniPath.hashCode() : 0);
        result = 31 * result + (setupScriptPath != null ? setupScriptPath.hashCode() : 0);
        result = 31 * result + phpCommandLineSettings.hashCode();
        return result;
    }

    @Override
    public TesterSettings clone() throws CloneNotSupportedException {
        return (TesterSettings) super.clone();
    }
}
