/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neumanb.nb.checkstyle.editor;

import com.puppycrawl.tools.checkstyle.api.SeverityLevel;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;

/**
 *
 * @author ben
 */
public class SevereAnnotation extends CheckstyleAnnotation {
    
    public SevereAnnotation(StyledDocument document, Position position, String shortDescription, SeverityLevel level) {
        super(document, position, shortDescription, level);
    }
    
    @Override
    public String getAnnotationType() {
        return "org-neumanb-nb-checkstyle-resources-checkstyle-severe-annotation"; // NOI18N
    }
}
