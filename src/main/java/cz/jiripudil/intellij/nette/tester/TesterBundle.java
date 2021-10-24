package cz.jiripudil.intellij.nette.tester;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.ResourceBundle;

public class TesterBundle {
    @NonNls private static final String BUNDLE_NAME = "messages.TesterBundle";
    @NotNull private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private TesterBundle() {
    }

    public static String message(@PropertyKey(resourceBundle = BUNDLE_NAME) String key, Object... params) {
        return AbstractBundle.message(BUNDLE, key, params);
    }
}
