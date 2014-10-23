package org.cdlflex.sf2pdf;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Collection;

import org.apache.maven.reporting.MavenReportException;
import org.cdlflex.sf2pdf.readers.surefire.ReportParser;
import org.cdlflex.sf2pdf.readers.surefire.model.Suite;
import org.junit.Test;

public class SurefireParserTest {

    @Test
    public void testParseWithRerunTestCase_shouldFixErrorsInSuite() throws MavenReportException {
        Collection<Suite> suites = ReportParser.parse(new File("src/test/resources/surefire-reports"));
        assertEquals(1, suites.size());
        Suite suite = (Suite) suites.toArray()[0];
        assertEquals(2, suite.getNumberOfTests());
        assertEquals(0, suite.getNumberOfErrors());
        assertEquals(0, suite.getNumberOfFailures());
        assertEquals(0, suite.getNumberOfSkipped());
    }
}
