package cz.jiripudil.intellij.nette.tester;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpClassHierarchyUtils;
import com.jetbrains.php.lang.PhpLangUtil;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import cz.jiripudil.intellij.nette.tester.configuration.TesterRunConfiguration;
import cz.jiripudil.intellij.nette.tester.configuration.TesterRunConfigurationType;
import cz.jiripudil.intellij.nette.tester.configuration.TesterTestMethodRunConfiguration;
import cz.jiripudil.intellij.nette.tester.configuration.TesterTestMethodRunConfigurationType;
import cz.jiripudil.intellij.nette.tester.projectSettings.TesterProjectSettings;
import cz.jiripudil.intellij.nette.tester.projectSettings.TesterProjectSettingsManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TesterUtil {
    public static String NOTIFICATION_GROUP = "Nette tester";

    public static boolean isTestClass(@NotNull PhpClass phpClass) {
        if (phpClass.isAbstract() || phpClass.isInterface() || phpClass.isTrait()) {
            return false;
        }

        final Ref<Boolean> isTestCase = new Ref<>(false);
        PhpClassHierarchyUtils.processSuperClasses(phpClass, true, true, phpClass1 -> {
            String superFQN = phpClass1.getSuperFQN();
            if (superFQN != null && PhpLangUtil.equalsClassNames("\\Tester\\TestCase", superFQN)) {
                isTestCase.set(true);
            }

            return !isTestCase.get();
        });

        return isTestCase.get();
    }

    public static TesterProjectSettings getTesterSettings(@NotNull Project project) {
        return TesterProjectSettingsManager.getInstance(project).getState();
    }

    public static boolean isTestMethod(@NotNull Method method) {
        return method.getContainingClass() != null
            && isTestClass(method.getContainingClass())
            && StringUtil.startsWith(method.getName(), "test")
            && method.getModifier().isPublic();
    }

    @Nullable
    public static TesterRunConfiguration getMainConfiguration(@NotNull Project project) {
        List<TesterRunConfiguration> configurations = TesterUtil.getRunConfigurations(project);
        return getMainConfiguration(project, configurations);
    }

    @Nullable
    public static TesterRunConfiguration getMainConfiguration(
            @NotNull Project project,
            @NotNull List<TesterRunConfiguration> configurations
    ) {
        @Nullable TesterRunConfiguration mainConfiguration = configurations.stream()
                .filter(configuration -> "tests".equals(configuration.getName()))
                .findAny()
                .orElse(null);
        if (mainConfiguration == null) {
            TesterUtil.doNotify(
                    TesterBundle.message("runConfiguration.mainConfiguration.missing.title"),
                    TesterBundle.message("runConfiguration.mainConfiguration.missing.description"),
                    NotificationType.ERROR,
                    project
            );
            return null;

        } else {
            try {
                mainConfiguration.checkConfiguration();
                return mainConfiguration;

            } catch (RuntimeConfigurationException ex) {
                TesterUtil.doNotify(
                        TesterBundle.message("runConfiguration.mainConfiguration.invalid.title"),
                        TesterBundle.message("runConfiguration.mainConfiguration.invalid.description"),
                        NotificationType.ERROR,
                        project
                );
            }
            return null;
        }
    }

    public static List<TesterRunConfiguration> getRunConfigurations(@NotNull Project  project) {
        List<TesterRunConfiguration> configurations = new ArrayList<TesterRunConfiguration>();
        List<RunnerAndConfigurationSettings> settings = RunManager.getInstance(project)
                .getConfigurationSettingsList(TesterRunConfigurationType.class);
        for (RunnerAndConfigurationSettings setting : settings) {
            RunConfiguration configuration = setting.getConfiguration();
            if (configuration instanceof TesterRunConfiguration) {
                configurations.add((TesterRunConfiguration) configuration);
            }
        }
        return configurations;
    }

    public static List<TesterTestMethodRunConfiguration> getMethodRunConfigurations(@NotNull Project  project) {
        List<TesterTestMethodRunConfiguration> configurations = new ArrayList<TesterTestMethodRunConfiguration>();
        List<RunnerAndConfigurationSettings> settings = RunManager.getInstance(project)
                .getConfigurationSettingsList(TesterTestMethodRunConfigurationType.class);
        for (RunnerAndConfigurationSettings setting : settings) {
            RunConfiguration configuration = setting.getConfiguration();
            if (configuration instanceof TesterTestMethodRunConfiguration) {
                configurations.add((TesterTestMethodRunConfiguration) configuration);
            }
        }
        return configurations;
    }

    /**
     * Returns a doc. comment from which Nette Tester reads annotations.
     * Nette Tester reads them from first unindented doc. comment.
     * @see <a href="https://github.com/nette/tester/blob/v1.7.1/src/Framework/Helpers.php#L42">Comment parsing</a>
     */
    @Nullable
    public static PhpDocComment findDocCommentRedByTester(PsiFile file) {
        for (PhpDocComment comment: PsiTreeUtil.findChildrenOfType(file, PhpDocComment.class)) {
            if (comment.getPrevSibling().getText().endsWith("\n")) {
                return comment;
            }
        }
        return null;
    }

    public static void doNotify(
            @NotNull String title,
            @NotNull @Nls(capitalization = Nls.Capitalization.Sentence) String content,
            @NotNull NotificationType type,
            @Nullable Project project
    ) {
        Notification notification = new Notification(NOTIFICATION_GROUP, title, content, type);
        doNotify(notification, project);
    }

    public static void doNotify(Notification notification, @Nullable Project project) {
        if (project != null && !project.isDisposed() && !project.isDefault()) {
            ((Notifications)project.getMessageBus().syncPublisher(Notifications.TOPIC)).notify(notification);
        } else {
            Application app = ApplicationManager.getApplication();
            if (!app.isDisposed()) {
                ((Notifications)app.getMessageBus().syncPublisher(Notifications.TOPIC)).notify(notification);
            }
        }
    }
}
