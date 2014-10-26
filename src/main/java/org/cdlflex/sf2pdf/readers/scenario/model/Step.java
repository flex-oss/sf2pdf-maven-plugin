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
package org.cdlflex.sf2pdf.readers.scenario.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

/**
 * This class represents a test step.
 */
public class Step implements Serializable {
    private static final long serialVersionUID = 1L;

    private String description;
    private String file;
    private Step parentStep;
    private List<Step> childSteps;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public Step getParentStep() {
        return parentStep;
    }

    public void setParentStep(Step parentStep) {
        this.parentStep = parentStep;
    }

    @XmlTransient
    public List<Step> getChildSteps() {
        return childSteps;
    }

    public void setChildSteps(List<Step> childSteps) {
        this.childSteps = childSteps;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
