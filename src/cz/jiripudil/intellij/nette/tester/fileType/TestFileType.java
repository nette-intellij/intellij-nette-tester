package cz.jiripudil.intellij.nette.tester.fileType;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.lang.PhpLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TestFileType extends LanguageFileType {
    public static final TestFileType INSTANCE = new TestFileType();
    public static final String DEFAULT_EXTENSION = "phpt";

    protected TestFileType() {
        super(PhpLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Nette Tester test";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Nette Tester test";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return PhpIcons.PHP_TEST_FILE;
    }
}
