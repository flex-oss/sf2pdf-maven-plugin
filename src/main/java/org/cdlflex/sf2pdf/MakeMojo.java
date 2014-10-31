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
package org.cdlflex.sf2pdf;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.reporting.MavenReportException;
import org.cdlflex.sf2pdf.readers.scenario.ScenarioParser;
import org.cdlflex.sf2pdf.readers.scenario.ScenarioParserException;
import org.cdlflex.sf2pdf.readers.scenario.model.ScenarioMap;
import org.cdlflex.sf2pdf.readers.surefire.ReportParser;
import org.cdlflex.sf2pdf.readers.surefire.model.Suite;
import org.cdlflex.sf2pdf.writers.ReportException;
import org.cdlflex.sf2pdf.writers.ReportGenerator;

import com.google.common.io.Files;

/**
 * Goal which touches a timestamp file.
 * 
 */
@Mojo(name = "make", defaultPhase = LifecyclePhase.PACKAGE)
public class MakeMojo extends AbstractMojo {

    /**
     * Location of the surefire reports.
     */
    @Parameter(defaultValue = "${project.build.directory}/surefire-reports", property = "reportDir")
    private File reportDirectory;

    /**
     * Location of the sf2pdf resources.
     */
    @Parameter(defaultValue = "src/test/resources/sf2pdf", property = "sf2pdfDir")
    private File sf2pdfDirectory;

    /**
     * Location of the produced report.
     */
    @Parameter(defaultValue = "${project.build.directory}", property = "outputDir")
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Starting sf2pdf-maven-plugin.");
        try {
            Collection<Suite> suites = ReportParser.parse(reportDirectory);
            ScenarioMap scenarios = ScenarioParser.parse(new File(sf2pdfDirectory + File.separator + "scenarios"));
            Properties properties = loadProperties();
            for (Suite suite : suites) {
                ReportGenerator generator = new ReportGenerator(outputDirectory, suite, scenarios, properties);
                generator.createReport();
            }
        } catch (MavenReportException | IOException | ReportException | ScenarioParserException e) {
            getLog().error(e);
            throw new MojoExecutionException("Error creating report ", e);
        }
        getLog().info("Finished sf2pdf-maven-plugin.");
    }

    private Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(Files.newReader(new File(sf2pdfDirectory + File.separator + "report.properties"),
                Charset.defaultCharset()));
        return properties;
    }
}
