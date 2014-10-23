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
            map = ScenarioParser.parse(new File("src/test/resources/scenarios"));
        } catch (ScenarioParserException e) {
            fail("Should not fail");
        }
        assertEquals(3, map.size());
    }
}
