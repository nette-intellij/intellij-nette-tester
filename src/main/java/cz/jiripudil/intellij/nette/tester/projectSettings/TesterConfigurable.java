package cz.jiripudil.intellij.nette.tester.projectSettings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import cz.jiripudil.intellij.nette.tester.projectSettings.editor.TesterConfigurableForm;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TesterConfigurable implements SearchableConfigurable, Configurable.NoScroll {
    private final Project project;
    private TesterConfigurableForm form;

    public TesterConfigurable(@NotNull final Project project) {
        this.project = project;
    }

    @NotNull
    @Override
    public String getId() {
        return this.getClass().getName();
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Nette Tester";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (form == null) {
            form = new TesterConfigurableForm(project);
        }

        return form.getComponent();
    }

    @Override
    public boolean isModified() {
        return form.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        form.apply();
    }

    @Override
    public void reset() {
        form.reset();
    }
}
