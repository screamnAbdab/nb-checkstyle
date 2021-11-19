package org.neumanb.nb.checkstyle;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.Context;
import com.puppycrawl.tools.checkstyle.api.FileSetCheck;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.api.MessageDispatcher;
import com.puppycrawl.tools.checkstyle.api.Violation;
import org.neumanb.nb.checkstyle.editor.CheckstyleTask;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The checkstyle checker that calls for the cancel status of the
 * {@link CheckstyleTask} and cancel itself if the task is cancelled.
 *
 * @see CheckstyleTask
 */
public class CancellableChecker extends Checker {

    private static final TreeSet<Violation> EMPTY_SET = new TreeSet<Violation>() {

        @Override
        public boolean add(Violation e) {
            throw new UnsupportedOperationException("Read only set");
        }

        @Override
        public boolean addAll(Collection<? extends Violation> c) {
            throw new UnsupportedOperationException("Read only set");
        }
    };

    private final CancellationHook hook;

    /**
     * Contructs the checker that won't do any checks whenewer the task has cancelled
     * status set to <code>true</code>.
     *
     * @param task the task that will be consulted for the cancellation
     * @throws CheckstyleException if any problem with initialization occurs
     */
    public CancellableChecker(CancellationHook hook) throws CheckstyleException {
        this.hook = hook;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addFileSetCheck(FileSetCheck fileSetCheck) {
        super.addFileSetCheck(new CancellableFileSetCheck(fileSetCheck, hook));
    }

    /**
     * Checks the given file.
     *
     * @param file file to check
     * @see Checker#process(File[])
     */
    public void process(File file) throws CheckstyleException {
        process(Collections.singletonList(file));
    }

    public static interface CancellationHook {

        boolean isCanceled();

    }

    private static class CancellableFileSetCheck implements FileSetCheck {

        private final FileSetCheck check;

        private final CancellationHook hook;

        public CancellableFileSetCheck(FileSetCheck check, CancellationHook hook) {
            this.check = check;
            this.hook = hook;
        }

        @Override
        public void contextualize(Context context) throws CheckstyleException {
            check.contextualize(context);
        }

        @Override
        public void configure(Configuration configuration) throws CheckstyleException {
            check.configure(configuration);
        }

        @Override
        public void setMessageDispatcher(MessageDispatcher dispatcher) {
            check.setMessageDispatcher(dispatcher);
        }

        @Override
        public SortedSet<Violation> process(File file, FileText ft) throws CheckstyleException {
            if (hook.isCanceled()) {
                return EMPTY_SET;
            }
            return check.process(file, ft);
        }
        
        @Override
        public void init() {
            check.init();
        }

        @Override
        public void finishProcessing() {
            check.finishProcessing();
        }

        @Override
        public void destroy() {
            check.destroy();
        }

        @Override
        public void beginProcessing(String charset) {
            check.beginProcessing(charset);
        }
     }
}
