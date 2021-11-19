package org.neumanb.nb.checkstyle;

import com.puppycrawl.tools.checkstyle.api.SeverityLevel;
import org.openide.util.NbBundle;

public enum Severity {

    ERROR {

        @Override
        public String toString() {
            return NbBundle.getMessage(Severity.class,
                    "CheckstyleSeverity.error.label");
        }

        @Override
        public boolean include(SeverityLevel level) {
            return level != SeverityLevel.IGNORE && level != SeverityLevel.INFO
                    && level != SeverityLevel.WARNING;
        }
    },

    WARNING {

        @Override
        public String toString() {
            return NbBundle.getMessage(Severity.class,
                    "CheckstyleSeverity.warning.label");
        }

        @Override
        public boolean include(SeverityLevel level) {
            return level != SeverityLevel.IGNORE && level != SeverityLevel.INFO;
        }
    },

    INFO {

        @Override
        public String toString() {
            return NbBundle.getMessage(Severity.class,
                    "CheckstyleSeverity.info.label");
        }

        @Override
        public boolean include(SeverityLevel level) {
            return level != SeverityLevel.IGNORE;
        }
    },

    IGNORE {

        @Override
        public String toString() {
            return NbBundle.getMessage(Severity.class,
                    "CheckstyleSeverity.ignore.label");
        }

        @Override
        public boolean include(SeverityLevel level) {
            return true;
        }
    };

    public abstract boolean include(SeverityLevel level);
}
