package org.neumanb.nb.checkstyle.options;

import org.netbeans.spi.options.AdvancedOption;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;

/**
 * Class implementing the advanced option for configuring the checkstyle plugin.
 *
 * @author Petr Hejl
 * @see OptionsPanelController
 */
public class CheckstyleOptions extends AdvancedOption {

    /**
     * Constructs the options object.
     */
    public CheckstyleOptions() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OptionsPanelController create() {
        return new CheckstyleOptionsController();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(CheckstyleOptionsPanel.class, "CheckstyleOptions.displayName");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTooltip() {
        return NbBundle.getMessage(CheckstyleOptionsPanel.class, "CheckstyleOptions.tooltip");
    }

}
