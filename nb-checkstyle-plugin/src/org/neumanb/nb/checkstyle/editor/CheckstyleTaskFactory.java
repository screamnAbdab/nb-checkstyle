package org.neumanb.nb.checkstyle.editor;

import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.JavaSource.Priority;
import org.netbeans.api.java.source.JavaSourceTaskFactory;
import org.netbeans.api.java.source.support.EditorAwareJavaSourceTaskFactory;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 */
@ServiceProvider(service=JavaSourceTaskFactory.class)
public class CheckstyleTaskFactory extends EditorAwareJavaSourceTaskFactory {

    public CheckstyleTaskFactory() {
        super(Phase.UP_TO_DATE, Priority.LOW);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CancellableTask<CompilationInfo> createTask(FileObject file) {
        return new CheckstyleTask(file);
    }

}
