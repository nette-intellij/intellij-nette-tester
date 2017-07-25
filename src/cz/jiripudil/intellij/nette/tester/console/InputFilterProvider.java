package cz.jiripudil.intellij.nette.tester.console;

import com.intellij.execution.filters.ConsoleInputFilterProvider;
import com.intellij.execution.filters.InputFilter;
import com.intellij.openapi.project.Project;
import cz.jiripudil.intellij.nette.tester.console.filters.InsertDiffLinkTextInputFilter;
import org.jetbrains.annotations.NotNull;


public class InputFilterProvider implements ConsoleInputFilterProvider {
    @NotNull
    @Override
    public InputFilter[] getDefaultFilters(@NotNull Project project) {
        return new InputFilter[]{
            new InsertDiffLinkTextInputFilter(),
        };
    }
}
