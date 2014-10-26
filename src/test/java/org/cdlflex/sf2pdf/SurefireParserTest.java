/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
