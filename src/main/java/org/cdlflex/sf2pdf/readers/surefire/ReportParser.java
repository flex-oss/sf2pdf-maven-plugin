/**
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.cdlflex.sf2pdf.readers.surefire;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.reporting.MavenReportException;
import org.apache.maven.surefire.shade.org.apache.maven.shared.utils.StringUtils;
import org.apache.maven.surefire.shade.org.apache.maven.shared.utils.io.DirectoryScanner;
import org.cdlflex.sf2pdf.readers.surefire.model.Suite;
import org.cdlflex.sf2pdf.readers.surefire.model.TestCase;
import org.cdlflex.sf2pdf.readers.surefire.model.TestStatus;
import org.xml.sax.SAXException;

/**
 * Modified copy of SurefireReportParser due to lack of its extendability.
 */
public final class ReportParser {

    private static final String INCLUDES = "*.xml";

    private static final String EXCLUDES =
        "*.txt, testng-failed.xml, testng-failures.xml, testng-results.xml, failsafe-summary*.xml";

    private ReportParser() {
    }

    /**
     * Scans a directory for XML test reports and parses them to a collection of {@link Suite}s.
     * 
     * @param reportDirectory the directory where XML reports are located.
     * @return a collection of {@link Suite}s.
     * @throws MavenReportException if the parsing goes wrong.
     */
    public static Collection<Suite> parse(File reportDirectory) throws MavenReportException {
        List<File> xmlReportFileList = scanForXmlReportFiles(reportDirectory);
        SuiteXmlParser parser = new SuiteXmlParser();
        Map<String, Suite> testSuites = new HashMap<>();
        for (File aXmlReportFileList : xmlReportFileList) {
            try {
                Suite suite = parser.parse(aXmlReportFileList.getAbsolutePath());
                testSuites.put(suite.getFullClassName(), suite);
            } catch (ParserConfigurationException e) {
                throw new MavenReportException("Error setting up parser for JUnit XML report", e);
            } catch (SAXException | IOException e) {
                throw new MavenReportException("Error reading JUnit XML report " + aXmlReportFileList, e);
            }
        }
        return mergeSuites(testSuites);
    }

    /**
     * Merges single testexecutions into existing suites if they are free of errors.
     * 
     * @param suiteMap
     */
    private static Collection<Suite> mergeSuites(Map<String, Suite> suiteMap) {
        List<String> deletes = new ArrayList<>();
        for (Suite suite : suiteMap.values()) {
            Map<String, List<TestCase>> testCases = suite.getTestCases();
            for (String key : testCases.keySet()) {
                Suite candidate = suiteMap.get(key);
                if (!(candidate == null || candidate.equals(suite))) {
                    if (allTestsOk(candidate)) {
                        suite.setNumberOfErrors(suite.getNumberOfErrors()
                            - countStati(testCases.get(key), TestStatus.ERROR));
                        suite.setNumberOfFailures(suite.getNumberOfFailures()
                            - countStati(testCases.get(key), TestStatus.FAILURE));
                        testCases.put(key, candidate.getTestCases().get(key));
                    }
                    deletes.add(key);
                }
            }
        }
        for (String delete : deletes) {
            suiteMap.remove(delete);
        }
        return suiteMap.values();
    }

    private static int countStati(List<TestCase> cases, TestStatus status) {
        int count = 0;
        for (TestCase cs : cases) {
            if (cs.getTestStatus().equals(status)) {
                count++;
            }
        }
        return count;
    }

    private static boolean allTestsOk(Suite suite) {
        return suite.getNumberOfErrors() == 0 && suite.getNumberOfFailures() == 0;
    }

    private static List<File> scanForXmlReportFiles(File reportDirectory) throws MavenReportException {
        List<File> xmlReportFileList = new ArrayList<>();
        if (!reportDirectory.exists()) {
            throw new MavenReportException("ReportsDirectory does not exist!: " + reportDirectory);
        }
        String[] xmlReportFiles = getIncludedFiles(reportDirectory, INCLUDES, EXCLUDES);
        for (String xmlReportFile : xmlReportFiles) {
            File xmlReport = new File(reportDirectory, xmlReportFile);
            xmlReportFileList.add(xmlReport);
        }
        return xmlReportFileList;
    }

    private static String[] getIncludedFiles(File directory, String includes, String excludes) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(directory);
        scanner.setIncludes(StringUtils.split(includes, ","));
        scanner.setExcludes(StringUtils.split(excludes, ","));
        scanner.scan();
        return scanner.getIncludedFiles();
    }
}
