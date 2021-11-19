package org.neumanb.nb.checkstyle;

import java.util.regex.Pattern;

/**
 *
 */
public final class Configuration {

    private final Severity severity;

    private final com.puppycrawl.tools.checkstyle.api.Configuration configuration;

    private final ClassLoader classLoader;

    private final Pattern ignoredPathsPattern;

    public Configuration(Severity severity,
            com.puppycrawl.tools.checkstyle.api.Configuration configuration,
            ClassLoader classLoader, Pattern ignoredPathsPattern) {
        this.severity = severity;
        this.configuration = configuration;
        this.classLoader = classLoader;
        this.ignoredPathsPattern = ignoredPathsPattern;
    }

    public com.puppycrawl.tools.checkstyle.api.Configuration getCheckstyleConfiguration() {
        return configuration;
    }

    public ClassLoader getCheckstyleClassLoader() {
        return classLoader;
    }

    public Severity getSeverity() {
        return severity;
    }

    public Pattern getIgnoredPathsPattern() {
        return ignoredPathsPattern;
    }

}
