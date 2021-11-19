package org.neumanb.nb.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public abstract class CheckstyleListener<T> implements AuditListener {

    private static final Logger LOGGER = Logger.getLogger(CheckstyleListener.class.getName());

    private final Severity minimalSeverity;

    /* GuardedBy("this") */
    private final List<T> results = new ArrayList<>();

    public CheckstyleListener(Severity minimalSeverity) {
        this.minimalSeverity = minimalSeverity;
    }

    public synchronized final List<T> getResults() {
        return results;
    }

    public abstract T createResult(AuditEvent evt);

    @Override
    public final void addError(AuditEvent evt) {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.log(Level.FINEST, "{0}: {1}", new Object[] {evt.getLine(), evt.getMessage()});
        }

        if (evt.getLine() <= 0) {
            return;
        }

        if (minimalSeverity.include(evt.getSeverityLevel())) {
            T result = createResult(evt);
            if (result != null) {
                synchronized (this) {
                    results.add(result);
                }
            }
        }
    }

    @Override
    public final void addException(AuditEvent evt, Throwable throwable) {
        LOGGER.log(Level.SEVERE, null, throwable);
    }

    @Override
    public final void auditFinished(AuditEvent evt) {
    }

    @Override
    public final void auditStarted(AuditEvent evt) {
    }

    @Override
    public final void fileFinished(AuditEvent evt) {
    }

    @Override
    public final void fileStarted(AuditEvent evt) {
    }
}
