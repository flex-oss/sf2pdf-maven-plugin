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
package org.cdlflex.sf2pdf.readers.surefire;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.cdlflex.sf2pdf.readers.surefire.model.Suite;
import org.cdlflex.sf2pdf.readers.surefire.model.TestCase;
import org.cdlflex.sf2pdf.readers.surefire.model.TestStatus;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A class that parses XML files to {@link Suite}s.
 */
public class SuiteXmlParser extends DefaultHandler {
    private Suite suite;

    private final NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);

    /**
     * @noinspection StringBufferField
     */
    private StringBuffer currentElement;

    private TestCase testCase;

    private boolean valid;

    /**
     * Parses a XML file at the {@code xmlPath} to a {@link Suite}.
     * 
     * @param xmlPath the path to the file as string.
     * @return a single {@link Suite}.
     * @throws ParserConfigurationException if the content could not be parsed.
     * @throws SAXException if the SAX reader could not read the file.
     * @throws IOException if the file could not be reached or opened.
     */
    public Suite parse(String xmlPath) throws ParserConfigurationException, SAXException, IOException {
        Path path = Paths.get(xmlPath);
        try (InputStream is = Files.newInputStream(path)) {
            return parse(is);
        }
    }

    /**
     * Parses a {@link InputStream} to a {@link Suite}.
     * 
     * @param stream the {@link InputStream}.
     * @return a single {@link Suite}.
     * @throws ParserConfigurationException if the content could not be parsed.
     * @throws SAXException if the SAX reader could not read the file.
     * @throws IOException if the file could not be reached or opened.
     */
    public Suite parse(InputStream stream) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        valid = true;
        saxParser.parse(new InputSource(stream), this);
        return suite;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (!valid) {
            return;
        }
        try {
            switch (qName) {
                case "testsuite":
                    suite = new Suite();

                    try {
                        Number time = numberFormat.parse(attributes.getValue("time"));
                        suite.setTimeElapsed(time.floatValue());
                    } catch (NullPointerException npe) {
                        System.err.println("WARNING: no time attribute found on testsuite element");
                    }

                    // check if group attribute is existing
                    if (attributes.getValue("group") != null && !"".equals(attributes.getValue("group"))) {
                        String packageName = attributes.getValue("group");
                        String name = attributes.getValue("name");

                        suite.setFullClassName(packageName + "." + name);
                    } else {
                        String fullClassName = attributes.getValue("name");
                        suite.setFullClassName(fullClassName);
                    }
                    break;
                case "property":
                    suite.addProperty(attributes.getValue("name"), attributes.getValue("value"));
                    break;
                case "testcase":
                    currentElement = new StringBuffer();
                    testCase = new TestCase();
                    testCase.setName(attributes.getValue("name"));
                    testCase.setClassName(attributes.getValue("classname"));
                    testCase.setFullName(testCase.getClassName() + "." + testCase.getName());

                    String timeAsString = attributes.getValue("time");
                    Number time = 0;
                    if (timeAsString != null) {
                        time = numberFormat.parse(timeAsString);
                    }
                    testCase.setTime(time.floatValue());
                    suite.setNumberOfTests(1 + suite.getNumberOfTests());
                    testCase.setTestStatus(TestStatus.PASS);
                    break;
                case "failure":
                    testCase.addCause(attributes.getValue("message"), attributes.getValue("type"));
                    testCase.setTestStatus(TestStatus.FAILURE);
                    suite.setNumberOfFailures(1 + suite.getNumberOfFailures());
                    break;
                case "error":
                    testCase.addCause(attributes.getValue("message"), attributes.getValue("type"));
                    testCase.setTestStatus(TestStatus.ERROR);
                    suite.setNumberOfErrors(1 + suite.getNumberOfErrors());
                    break;
                case "skipped":
                    final String message = attributes.getValue("message");
                    testCase.addCause(message != null ? message : "skipped", "skipped");
                    testCase.setTestStatus(TestStatus.SKIP);
                    suite.setNumberOfSkipped(1 + suite.getNumberOfSkipped());
                    break;
                case "failsafe-summary":
                    valid = false;
                    break;
                default:
            }
        } catch (ParseException e) {
            throw new SAXException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("testcase".equals(qName)) {
            suite.addTestCase(testCase);
        } else if ("failure".equals(qName)) {
            Map<String, Object> failure = testCase.getCause();
            failure.put("detail", parseCause(currentElement.toString()));
        } else if ("error".equals(qName)) {
            Map<String, Object> error = testCase.getCause();
            error.put("detail", parseCause(currentElement.toString()));
        } else if ("time".equals(qName)) {
            try {
                Number time = numberFormat.parse(currentElement.toString());
                suite.setTimeElapsed(time.floatValue());
            } catch (ParseException e) {
                throw new SAXException(e.getMessage(), e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (!valid) {
            return;
        }
        String s = new String(ch, start, length);
        if (!"".equals(s.trim())) {
            currentElement.append(s);
        }
    }

    private List<String> parseCause(String detail) {
        String fullName = testCase.getFullName();
        String name = fullName.substring(fullName.lastIndexOf(".") + 1);
        return parseCause(detail, name);
    }

    private List<String> parseCause(String detail, String compareTo) {
        StringTokenizer stringTokenizer = new StringTokenizer(detail, "\n");
        List<String> parsedDetail = new ArrayList<String>(stringTokenizer.countTokens());

        while (stringTokenizer.hasMoreTokens()) {
            String lineString = stringTokenizer.nextToken().trim();
            parsedDetail.add(lineString);
            if (lineString.contains(compareTo)) {
                break;
            }
        }
        return parsedDetail;
    }

    public boolean isValid() {
        return valid;
    }
}
