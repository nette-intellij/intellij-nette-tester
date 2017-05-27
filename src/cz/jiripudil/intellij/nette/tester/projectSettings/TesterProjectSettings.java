package cz.jiripudil.intellij.nette.tester.projectSettings;

import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import com.jetbrains.php.util.PhpConfigurationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Tag("testerSettings")
public class TesterProjectSettings {
    @NotNull private String defaultExtension = "phpt";
    @Nullable private String bootstrapFile;
    @NotNull private List<TesterNamespaceMapping> namespaceMappings = new ArrayList<>();


    @Attribute("defaultExtension")
    @NotNull
    public String getDefaultExtension() {
        return defaultExtension;
    }

    public void setDefaultExtension(@NotNull String defaultExtension) {
        this.defaultExtension = defaultExtension;
    }


    @Attribute("bootstrapFile")
    @Nullable
    public String getBootstrapFile() {
        return PhpConfigurationUtil.deserializePath(bootstrapFile);
    }

    public void setBootstrapFile(@Nullable String bootstrapFile) {
        this.bootstrapFile = PhpConfigurationUtil.serializePath(bootstrapFile);
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
