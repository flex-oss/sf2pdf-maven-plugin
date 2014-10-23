package org.cdlflex.sf2pdf.readers.scenario;

/**
 * An exception that is thrown if problems with the parsing occur.
 */
public class ScenarioParserException extends Exception {
    private static final long serialVersionUID = 1L;

    public ScenarioParserException(Exception e) {
        super(e);
    }
}
