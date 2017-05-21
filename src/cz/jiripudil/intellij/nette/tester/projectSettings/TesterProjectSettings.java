package cz.jiripudil.intellij.nette.tester.projectSettings;

import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Tag("testerSettings")
public class TesterProjectSettings {
    @NotNull private String defaultExtension = "phpt";
    @NotNull private List<TesterNamespaceMapping> namespaceMappings = new ArrayList<>();


    @Attribute("defaultExtension")
    @NotNull
    public String getDefaultExtension() {
        return defaultExtension;
    }

    public void setDefaultExtension(@NotNull String defaultExtension) {
        this.defaultExtension = defaultExtension;
    }


    @Tag("namespaceMappings")
    @NotNull
    public List<TesterNamespaceMapping> getNamespaceMappings() {
        return namespaceMappings;
    }

    public void setNamespaceMappings(@NotNull List<TesterNamespaceMapping> namespaceMappings) {
        this.namespaceMappings = namespaceMappings;
    }
}
