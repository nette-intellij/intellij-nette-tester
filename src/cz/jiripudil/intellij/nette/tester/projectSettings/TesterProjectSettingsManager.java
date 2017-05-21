package cz.jiripudil.intellij.nette.tester.projectSettings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "Tester",
    storages = {@Storage("tester.xml")}
)
public class TesterProjectSettingsManager implements PersistentStateComponent<TesterProjectSettings> {
    private TesterProjectSettings settings;

    public TesterProjectSettingsManager() {
        this.settings = new TesterProjectSettings();
    }

    @NotNull
    public static TesterProjectSettingsManager getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, TesterProjectSettingsManager.class);
    }

    @Nullable
    @Override
    public TesterProjectSettings getState() {
        return settings;
    }

    @Override
    public void loadState(TesterProjectSettings settings) {
        this.settings = settings;
    }
}
