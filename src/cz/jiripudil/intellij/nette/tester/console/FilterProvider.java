package cz.jiripudil.intellij.nette.tester.console;

import com.intellij.execution.filters.ConsoleFilterProvider;
import com.intellij.execution.filters.Filter;
import com.intellij.openapi.project.Project;
import cz.jiripudil.intellij.nette.tester.console.filters.MakeDiffLinkTextClickableFilter;
import org.jetbrains.annotations.NotNull;


public class FilterProvider implements ConsoleFilterProvider {
    @NotNull
    @Override
    public Filter[] getDefaultFilters(@NotNull Project project) {
        return new Filter[]{
            new MakeDiffLinkTextClickableFilter(),
        };
    }
}
