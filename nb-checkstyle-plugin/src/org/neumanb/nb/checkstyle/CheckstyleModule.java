package org.neumanb.nb.checkstyle;

import org.neumanb.nb.checkstyle.editor.CheckstyleAnnotationContainer;
import java.awt.Dimension;
import java.util.prefs.Preferences;
import javax.swing.JLabel;
import javax.swing.text.View;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 *
 * @author Petr Hejl
 */
public class CheckstyleModule extends ModuleInstall {

    private static final String PROP_CHECKSTYLE_MESSAGE56_SHOWN =
        "cz.sickboy.netbeans.checkstyle.message56.shown"; // NOI18N

    @Override
    public void restored() {
        final Preferences prefs = NbPreferences.forModule(CheckstyleModule.class);
        boolean shown = prefs.getBoolean(PROP_CHECKSTYLE_MESSAGE56_SHOWN, false);
        if (!shown && CheckstyleSettings.getDefault().getValues().getCustomConfigFile() != null) {
            WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

                @Override
                public void run() {
                    prefs.putBoolean(PROP_CHECKSTYLE_MESSAGE56_SHOWN, true);
                    String message = NbBundle.getMessage(CheckstyleModule.class, "CheckstyleModule.message56");
                    JLabel label = new JLabel(message);
                    label.setPreferredSize(getPreferredSize(message, 400));
                    NotifyDescriptor desc = new NotifyDescriptor.Message(label);
                    DialogDisplayer.getDefault().notify(desc);
                }
            });
        }
    }


    @Override
    public void uninstalled() {
        CheckstyleAnnotationContainer.reset();
    }

    private static Dimension getPreferredSize(String html, int width) {
        JLabel test = new JLabel(html);

        Object object = test.getClientProperty(
                javax.swing.plaf.basic.BasicHTML.propertyKey);
        if (object instanceof View) {
            View view = (View) object;

            view.setSize(width, 0);

            float w = view.getPreferredSpan(View.X_AXIS);
            float h = view.getPreferredSpan(View.Y_AXIS);

            return new Dimension((int) Math.ceil(w), (int) Math.ceil(h));
        } else {
            // some reasonable default :(
            return new Dimension(width, (int) Math.ceil(0.75 * width));
        }
    }

}
