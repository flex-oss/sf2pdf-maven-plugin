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
package org.cdlflex.sf2pdf.readers.surefire.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.cdlflex.sf2pdf.readers.scenario.model.TestScenario;

/**
 * Convenience class to map from the unique className of a test class to a List of tests contained in that class.
 */
public class TestCaseMap extends HashMap<String, List<TestCase>> {
    private static final long serialVersionUID = 1L;

    public List<TestCase> getSortedList() {
        List<TestCase> result = new ArrayList<>();
        for (List<TestCase> testCases : this.values()) {
            result.addAll(testCases);
        }
        Collections.sort(result, new TestCaseComparator());
        return result;
    }

    private class TestCaseComparator implements Comparator<TestCase> {

        @Override
        public int compare(TestCase tc1, TestCase tc2) {
            TestScenario scenario1 = tc1.getScenario();
            TestScenario scenario2 = tc2.getScenario();
            if (scenario1 == null && scenario2 == null) {
                return 0;
            } else if (scenario1 == null) {
                return -1;
            } else if (scenario2 == null) {
                return 1;
            } else {
                return Long.compare(scenario1.getScenarioNumber(), scenario2.getScenarioNumber());
            }
        }
    }
}
