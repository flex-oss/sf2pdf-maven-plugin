package org.cdlflex.sf2pdf.writers.itextpdf;

/**
 * A specialized list for the report generation.
 */
public class ReportList extends com.itextpdf.text.List {

    public ReportList(boolean number, float floatIndent) {
        super(number, floatIndent);
        setPreSymbol("   ");
        setPostSymbol("   ");
        setListSymbol("*   ");
    }
}
