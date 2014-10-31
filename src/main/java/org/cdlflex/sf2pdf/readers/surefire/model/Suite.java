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
package org.cdlflex.sf2pdf.readers.surefire.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cdlflex.sf2pdf.util.Format;

/**
 * ReportTestSuite extended with properties.
 */
public class Suite {
    private Map<String, String> properties = new HashMap<>();

    private TestCaseMap testCases = new TestCaseMap();

    private int numberOfErrors;

    private int numberOfFailures;

    private int numberOfSkipped;

    private int numberOfTests;

    private String name;

    private String fullClassName;

    private String packageName;

    private float timeElapsed;

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    /**
     * Adds a {@code key} and a {@code value} to the suite.
     * 
     * @param key the key.
     * @param value the value.
     */
    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    /**
     * Adds a {@link TestCase} to the suite.
     * 
     * @param testCase the {@link TestCase} to add.
     */
    public void addTestCase(TestCase testCase) {
        String key = testCase.getClassName();
        List<TestCase> existingCases = testCases.get(key);
        if (existingCases == null) {
            existingCases = new ArrayList<>();
            testCases.put(key, existingCases);
        }
        existingCases.add(testCase);
    }

    public int getNumberOfPassed() {
        return numberOfTests - numberOfErrors - numberOfFailures - numberOfSkipped;
    }

    public TestCaseMap getTestCases() {
        return testCases;
    }

    public void setTestCases(TestCaseMap reportTestCases) {
        this.testCases = reportTestCases;
    }

    public int getNumberOfErrors() {
        return numberOfErrors;
    }

    public void setNumberOfErrors(int numberOfErrors) {
        this.numberOfErrors = numberOfErrors;
    }

    public int getNumberOfFailures() {
        return numberOfFailures;
    }

    public void setNumberOfFailures(int numberOfFailures) {
        this.numberOfFailures = numberOfFailures;
    }

    public int getNumberOfSkipped() {
        return numberOfSkipped;
    }

    public void setNumberOfSkipped(int numberOfSkipped) {
        this.numberOfSkipped = numberOfSkipped;
    }

    public int getNumberOfTests() {
        return numberOfTests;
    }

    public void setNumberOfTests(int numberOfTests) {
        this.numberOfTests = numberOfTests;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    /**
     * Sets the full class name for this suite.
     * 
     * @param fullClassName the fully qualified name.
     */
    public void setFullClassName(String fullClassName) {
        this.fullClassName = fullClassName;
        int lastDotPosition = fullClassName.lastIndexOf(".");

        name = fullClassName.substring(lastDotPosition + 1, fullClassName.length());

        if (lastDotPosition < 0) {
            /* no package name */
            packageName = "";
        } else {
            packageName = fullClassName.substring(0, lastDotPosition);
        }
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public float getTimeElapsed() {
        return this.timeElapsed;
    }

    public void setTimeElapsed(float timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public Double getFailedPercentage() {
        return Math.round(1D * numberOfFailures / numberOfTests * 10000) / 100D;
    }

    public Double getPassedPercentage() {
        return Math.round(1D * getNumberOfPassed() / numberOfTests * 10000) / 100D;
    }

    public Double getErrorPercentage() {
        return Math.round(1D * numberOfErrors / numberOfTests * 10000) / 100D;
    }

    public Double getSkippedPercentage() {
        return Math.round(1D * numberOfSkipped / numberOfTests * 10000) / 100D;
    }

    public String getTotalDurationFormatted() {
        return Format.duration(timeElapsed);
    }
}
