package org.cdlflex.sf2pdf.writers.itextpdf;

import com.itextpdf.text.Paragraph;

/**
 * A class that represents an indented paragraph.
 */
public class IndentedParagraph extends Paragraph {
    private static final long serialVersionUID = 1L;

    private static final float DIST_PARA_LEFT = 25f;
    private static final float DIST_PARA_RIGHT = 25f;

    public IndentedParagraph(float leading) {
        super(leading);
        setIndentation();
    }

    public IndentedParagraph() {
        setIndentation();
    }

    private void setIndentation() {
        setIndentationLeft(DIST_PARA_LEFT);
        setIndentationRight(DIST_PARA_RIGHT);
    }
}
