package org.cdlflex.sf2pdf.writers;

/**
 * An exception that occurs during reporting.
 */
public class ReportException extends Exception {
    private static final long serialVersionUID = 1L;

    public ReportException() {
    }

    public ReportException(String message) {
        super(message);
    }

    public ReportException(Throwable t) {
        super(t);
    }
}
