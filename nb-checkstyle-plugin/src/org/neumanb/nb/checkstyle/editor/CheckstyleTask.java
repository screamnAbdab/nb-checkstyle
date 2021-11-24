package org.neumanb.nb.checkstyle.editor;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import org.neumanb.nb.checkstyle.CancellableChecker;
import org.neumanb.nb.checkstyle.CheckstyleListener;
import org.neumanb.nb.checkstyle.Configuration;
import org.neumanb.nb.checkstyle.ConfigurationLoader;
import org.neumanb.nb.checkstyle.Severity;
import org.neumanb.nb.checkstyle.error.ErrorHandler;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationInfo;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;
import org.openide.util.RequestProcessor;

/**
 *
 */
public class CheckstyleTask implements CancellableTask<CompilationInfo>, CancellableChecker.CancellationHook {

    private static final Logger LOGGER = Logger.getLogger(CheckstyleTask.class.getName());

    private static final RequestProcessor THREAD_POOL = new RequestProcessor(CheckstyleTask.class.getName(), 10, true);

    private final FileObject fileObject;

    private Future<?> running;

    private boolean cancelled;

    public CheckstyleTask(FileObject fileObject) {
        this.fileObject = fileObject;
    }

    @Override
    public synchronized void cancel() {
        if (running != null) {
            running.cancel(true);
        }
        cancelled = true;
    }

    @Override
    public void run(CompilationInfo info) throws Exception {
        init();

        DataObject data = DataObject.find(fileObject);
        if (data == null || data.isModified()) {
            return;
        }

        EditorCookie editor = DataObject.find(fileObject).getLookup().lookup(EditorCookie.class);
        if (editor == null) {
            return;
        }

        try {
            Configuration config = ConfigurationLoader.getDefault().getConfiguration();

            final File file = FileUtil.toFile(fileObject);
            if (file == null) { // occurs for libraries for example
                return;
            }

            Pattern ignored = config.getIgnoredPathsPattern();
            if (ignored != null && ignored.matcher(file.getAbsolutePath()).matches()) {
                return;
            }

            List<CheckstyleAnnotation> results = run(fileObject, file, editor.openDocument(), config);
            if (!isCanceled()) {
                setAnnotations(fileObject, results);
            }
        } catch (CheckstyleException ex) {
            ErrorHandler.getDefault().handleError(fileObject, LOGGER, ex);
        }
    }

    synchronized void init() {
        running = null;
        cancelled = false;
    }

    public synchronized boolean isCanceled() {
        return cancelled;
    }

    private List<CheckstyleAnnotation> run(final FileObject fileObject, final File file,
            final StyledDocument document, final Configuration config) throws CheckstyleException {

        final CollectingListener listener = new CollectingListener(config.getSeverity(),
                document);

        Future<?> future;
        synchronized (this) {
            if (isCanceled()) {
                return Collections.emptyList();
            }

            running = THREAD_POOL.submit(new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
                    try {
                        Thread.currentThread().setContextClassLoader(config.getCheckstyleClassLoader());
                        CancellableChecker checker = new CancellableChecker(CheckstyleTask.this);
                        try {
                            // classloader to load checks
                            checker.setModuleClassLoader(config.getCheckstyleClassLoader());
                            // classloader to load classpath
                            //ClassPath path = ClassPath.getClassPath(fileObject, ClassPath.EXECUTE);
                            //if (path != null) {
                            //    checker.setClassLoader(path.getClassLoader(true));
                            //}
                            checker.configure(config.getCheckstyleConfiguration());
                            checker.addListener(listener);

                            checker.process(file);
                        } finally {
                            checker.destroy();
                        }

                        return null;
                    } finally {
                        Thread.currentThread().setContextClassLoader(originalClassLoader);
                    }
                }
            });
            future = running;
        }
        try {
            future.get();
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof CheckstyleException) {
                throw (CheckstyleException) cause;
            } else {
                throw new RuntimeException(cause);
            }
        } catch (InterruptedException ex) {
            LOGGER.log(Level.FINE, null, ex);
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        } catch (CancellationException ex) {
            // XXX is there a better way than catching runtime exception
            return Collections.emptyList();
        }

        return listener.getResults();
    }

    private static void setAnnotations(FileObject fileObject, List<CheckstyleAnnotation> annotations) {
        CheckstyleAnnotationContainer container = CheckstyleAnnotationContainer.getInstance(fileObject);
        if (container != null) {
            container.setAnnotations(annotations);
        } else {
            LOGGER.log(Level.INFO, "No annotation container"); // NOI18N
        }
    }

    private static Position getPosition(final StyledDocument document, final int lineNumber) {
        final AtomicReference<Position> ref = new AtomicReference<>();
        document.render(new Runnable() {

            @Override
            public void run() {
                int offset = NbDocument.findLineOffset(document, lineNumber);
                if (offset < 0 || offset >= document.getLength()) {
                    return;
                }

                try {
                    ref.set(document.createPosition(offset - NbDocument.findLineColumn(document, offset)));
                } catch (BadLocationException ex) {
                    LOGGER.log(Level.INFO, null, ex);
                }
            }
        });

        return ref.get();
    }

    private static class CollectingListener extends CheckstyleListener<CheckstyleAnnotation> {

        private final StyledDocument document;

        public CollectingListener(Severity minimalSeverity, StyledDocument document) {
            super(minimalSeverity);
            this.document = document;
        }

        @Override
        public CheckstyleAnnotation createResult(AuditEvent evt) {
            Position position = getPosition(document, evt.getLine() - 1);
            if (position != null) {
                return new SevereAnnotation(document, position,
                            evt.getMessage(), evt.getSeverityLevel());                
            }
            return null;
        }
    }
}
