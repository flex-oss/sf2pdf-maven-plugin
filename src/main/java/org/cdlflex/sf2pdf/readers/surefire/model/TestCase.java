package org.cdlflex.sf2pdf.readers.surefire.model;

import java.util.HashMap;
import java.util.Map;

import org.cdlflex.sf2pdf.readers.scenario.model.TestScenario;
import org.cdlflex.sf2pdf.util.Format;

public class TestCase {
    private String fullClassName;

    private String className;

    private String fullName;

    private String name;

    private float time;

    private TestScenario scenario;

    private TestStatus testStatus;

    private Map<String, Object> cause;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    public void setFullClassName(String fullClassName) {
        this.fullClassName = fullClassName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public Map<String, Object> getCause() {
        return cause;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void addCause(String message, String type) {
        cause = new HashMap<String, Object>();
        cause.put("message", message);
        cause.put("type", type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return fullName;
    }

    public TestStatus getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(TestStatus testStatus) {
        this.testStatus = testStatus;
    }

    public TestScenario getScenario() {
        return scenario;
    }

    public void setScenario(TestScenario scenario) {
        this.scenario = scenario;
    }

    public String getNiceDuration() {
        return Format.duration(time);
    }
}
