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
package org.cdlflex.sf2pdf.readers.scenario;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.maven.surefire.shade.org.apache.maven.shared.utils.io.DirectoryScanner;
import org.cdlflex.sf2pdf.readers.scenario.model.ScenarioMap;
import org.cdlflex.sf2pdf.readers.scenario.model.TestScenario;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parses scenario files that describe the sequence how a test is done.
 */
public class ScenarioParser extends DefaultHandler {

    /**
     * Parses the scenario files of a folder.
     * 
     * @param scenarioDirectory the folder where the scenario files are located.
     * @return a {@link ScenarioMap} with a scenario key and the corresponding scenario.
     * @throws ScenarioParserException if the parsing goes wrong.
     */
    public static ScenarioMap parse(File scenarioDirectory) throws ScenarioParserException {
        ScenarioMap map = new ScenarioMap();
        String[] files = getFiles(scenarioDirectory);
        for (String file : files) {
            Path path = Paths.get(scenarioDirectory.getPath(), file);
            try (InputStream is = Files.newInputStream(path)) {
                TestScenario scenario = deserializeXML(is);
                map.put(createScenarioKey(scenario), scenario);
            } catch (JAXBException | IOException e) {
                throw new ScenarioParserException(e);
            }
        }
        return map;
    }

    private static String createScenarioKey(TestScenario scenario) {
        return scenario.getTestClass() + "." + scenario.getTestName();
    }

    private static TestScenario deserializeXML(InputStream in) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(TestScenario.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        TestScenario scenario = (TestScenario) unmarshaller.unmarshal(in);
        return scenario;
    }

    /**
     * Serializes a {@link TestScenario} to a XML file.
     * 
     * @param filename the filename of the file to serialize to.
     * @param scenario the {@link TestScenario} to serialize.
     * @throws ScenarioParserException if the serialization goes wrong.
     */
    public static void serializeXML(String filename, TestScenario scenario) throws ScenarioParserException {
        Path p = Paths.get(filename);
        try {
            Files.createDirectories(p.getParent());
            Files.createFile(p);
            try (OutputStream os = Files.newOutputStream(p)) {
                JAXBContext context = JAXBContext.newInstance(scenario.getClass());
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(scenario, os);
            } catch (JAXBException | IOException e) {
                throw new ScenarioParserException(e);
            }
        } catch (IOException e) {
            throw new ScenarioParserException(e);
        }
    }

    private static String[] getFiles(File directory) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(directory);
        scanner.scan();
        return scanner.getIncludedFiles();
    }
}
