package org.neumanb.nb.checkstyle.editor;

import com.puppycrawl.tools.checkstyle.api.SeverityLevel;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import org.openide.text.Annotation;
import org.openide.text.NbDocument;

public abstract class CheckstyleAnnotation extends Annotation {

    /**
     * document.
     */
    private final StyledDocument document;

    private final Position position;

    private final String shortDescription;

    private final SeverityLevel level;

    public CheckstyleAnnotation(StyledDocument document, Position position,
            String shortDescription, SeverityLevel level) {

        this.document = document;
        this.position = position;
        this.shortDescription = shortDescription;
        this.level = level;
    }

    public void documentAttach() {
        NbDocument.addAnnotation(document, position, -1, this);
    }

    public void documentDetach() {
        NbDocument.removeAnnotation(document, this);
    }

    @Override
    public String getShortDescription() {
        return shortDescription;
    }

}
