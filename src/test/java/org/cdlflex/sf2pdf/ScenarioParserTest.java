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
package org.cdlflex.sf2pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.cdlflex.sf2pdf.readers.scenario.ScenarioParser;
import org.cdlflex.sf2pdf.readers.scenario.ScenarioParserException;
import org.cdlflex.sf2pdf.readers.scenario.model.ScenarioMap;
import org.junit.Test;

public class ScenarioParserTest {

    @Test
    public void testParse() {
        ScenarioMap map = new ScenarioMap();
        try {
            map = ScenarioParser.parse(new File("src/test/resources/sf2pdf/scenarios"));
        } catch (ScenarioParserException e) {
            fail("Should not fail");
        }
        assertEquals(3, map.size());
    }
}
