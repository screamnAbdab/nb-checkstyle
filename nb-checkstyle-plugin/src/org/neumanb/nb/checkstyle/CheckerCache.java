package org.neumanb.nb.checkstyle;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.java.classpath.ClassPath;
import org.openide.filesystems.FileObject;

/**
 *
 */
public final class CheckerCache {

    private static final Logger LOGGER = Logger.getLogger(CheckerCache.class.getName());

    private Configuration lastConfiguration;

    private ClassLoader lastClassLoader;

    private Checker lastChecker;

    private boolean inUse;

    public Checker acquireChecker(FileObject fileObject, Configuration configuration) throws CheckstyleException {
        ClassLoader classLoader = null;
        ClassPath path = ClassPath.getClassPath(fileObject, ClassPath.EXECUTE);
        if (path != null) {
            classLoader = path.getClassLoader(true);
        }

        synchronized (this) {
            if ((lastClassLoader == classLoader || (lastClassLoader != null && lastClassLoader.equals(classLoader)))
                    && configuration.equals(lastConfiguration) && !inUse) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Cache hit for {0}", fileObject.getNameExt());
                }
                inUse = true;
                return lastChecker;
            } else if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Cache miss for {0}", fileObject.getNameExt());
            }
        }

        Checker freshChecker = new Checker();
        // classloader to load checks
        freshChecker.setModuleClassLoader(configuration.getCheckstyleClassLoader());
        // classloader to load classpath
        //freshChecker.setClassloader(classLoader);

        freshChecker.configure(configuration.getCheckstyleConfiguration());

        synchronized (this) {
            lastConfiguration = configuration;
            lastClassLoader = classLoader;
            lastChecker = freshChecker;
            inUse = true;
        }

        return freshChecker;
    }

    public void releaseChecker(Checker checker) {
        synchronized (this) {
            if (checker.equals(lastChecker)) {
                inUse = false;
            } else {
                checker.destroy();
            }
        }
    }

    public void clear() {
        Checker current;
        synchronized (this) {
           current = lastChecker;

           lastConfiguration = null;
           lastClassLoader = null;
           lastChecker = null;
           inUse = false;
        }
        if (current != null) {
            current.destroy();
        }
    }

}
