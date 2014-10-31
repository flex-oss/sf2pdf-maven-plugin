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
package org.cdlflex.sf2pdf.readers.scenario.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestScenario implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long scenarioNumber;
    private String name;
    private String testName;
    private String testClass;
    private List<Step> steps;
    private List<String> preconditions;
    private List<String> expectedResults;
    private TestCommitter testCommitter;
    private TestCategory testCategory;
    private TestPriority testPriority;
    private List<Tag> tags;

    public Long getScenarioNumber() {
        return scenarioNumber;
    }

    public void setScenarioNumber(Long scenarioNumber) {
        this.scenarioNumber = scenarioNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElementWrapper(name = "steps")
    @XmlElement(name = "step")
    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public void removeSteps() {
        this.steps.clear();
    }

    @XmlElementWrapper(name = "preconditions")
    @XmlElement(name = "precondition")
    public List<String> getPreconditions() {
        return preconditions;
    }

    public void setPreconditions(List<String> preconditions) {
        this.preconditions = preconditions;
    }

    @XmlElementWrapper(name = "expectedResults")
    @XmlElement(name = "expectedResult")
    public List<String> getExpectedResults() {
        return expectedResults;
    }

    public void setExpectedResults(List<String> expectedResults) {
        this.expectedResults = expectedResults;
    }

    public TestCommitter getTestCommitter() {
        return testCommitter;
    }

    public void setTestCommitter(TestCommitter testCommitter) {
        this.testCommitter = testCommitter;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public TestCategory getTestCategory() {
        return testCategory;
    }

    public void setTestCategory(TestCategory testCategory) {
        this.testCategory = testCategory;
    }

    public TestPriority getTestPriority() {
        return testPriority;
    }

    public void setTestPriority(TestPriority testPriority) {
        this.testPriority = testPriority;
    }

    public void setTestClass(String testClass) {
        this.testClass = testClass;
    }

    public String getTestClass() {
        return testClass;
    }

    @XmlElementWrapper(name = "tags")
    @XmlElement(name = "tag")
    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void removeTags() {
        this.tags.clear();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (scenarioNumber == null ? 0 : scenarioNumber.hashCode());
        result = prime * result + (testCategory == null ? 0 : testCategory.hashCode());
        result = prime * result + (testClass == null ? 0 : testClass.hashCode());
        result = prime * result + (testCommitter == null ? 0 : testCommitter.hashCode());
        result = prime * result + (testName == null ? 0 : testName.hashCode());
        result = prime * result + (testPriority == null ? 0 : testPriority.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TestScenario)) {
            return false;
        }
        TestScenario other = (TestScenario) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (scenarioNumber == null) {
            if (other.scenarioNumber != null) {
                return false;
            }
        } else if (!scenarioNumber.equals(other.scenarioNumber)) {
            return false;
        }
        if (testCategory != other.testCategory) {
            return false;
        }
        if (testClass == null) {
            if (other.testClass != null) {
                return false;
            }
        } else if (!testClass.equals(other.testClass)) {
            return false;
        }
        if (testCommitter != other.testCommitter) {
            return false;
        }
        if (testName == null) {
            if (other.testName != null) {
                return false;
            }
        } else if (!testName.equals(other.testName)) {
            return false;
        }
        if (testPriority != other.testPriority) {
            return false;
        }
        return true;
    }
}
