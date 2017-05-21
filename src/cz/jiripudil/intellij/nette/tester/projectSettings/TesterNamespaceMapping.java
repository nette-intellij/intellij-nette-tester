package cz.jiripudil.intellij.nette.tester.projectSettings;

import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;

@Tag("testNamespaceMapping")
public class TesterNamespaceMapping {
    @NotNull private String sourceNamespace;
    @NotNull private String testsNamespace;

    private TesterNamespaceMapping(@NotNull String sourceNamespace, @NotNull String testsNamespace) {
        this.sourceNamespace = sourceNamespace;
        this.testsNamespace = testsNamespace;
    }

    public TesterNamespaceMapping() {
        this("", "");
    }

    @Attribute("sourceNamespace")
    @NotNull
    public String getSourceNamespace() {
        return sourceNamespace;
    }

    public void setSourceNamespace(@NotNull String sourceNamespace) {
        this.sourceNamespace = sourceNamespace;
    }

    @Attribute("testsNamespace")
    @NotNull
    public String getTestsNamespace() {
        return testsNamespace;
    }

    public void setTestsNamespace(@NotNull String testsNamespace) {
        this.testsNamespace = testsNamespace;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof TesterNamespaceMapping)) return false;

        TesterNamespaceMapping that = (TesterNamespaceMapping) other;

        return sourceNamespace.equals(that.sourceNamespace)
            && testsNamespace.equals(that.testsNamespace);
    }

    @Override
    public int hashCode() {
        int result = sourceNamespace.hashCode();
        result = 31 * result + testsNamespace.hashCode();
        return result;
    }

    @Override
    public TesterNamespaceMapping clone() {
        return new TesterNamespaceMapping(
            this.sourceNamespace,
            this.testsNamespace
        );
    }
}
