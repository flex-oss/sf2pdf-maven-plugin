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
@Mojo(name = "make", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class MakeMojo extends AbstractMojo {

    /**
     * Location of the surefire reports.
     */
    @Parameter(defaultValue = "${project.build.directory}/surefire-reports", property = "reportDir", required = true)
    private File reportDirectory;

    /**
     * Location of the scenario descriptions for the reports.
     */
    @Parameter(defaultValue = "", property = "scenarioDir", required = true)
    private File scenarioDirectory;

    /**
     * The location of the file containing Properties of the tested App.
     */
    @Parameter(defaultValue = "", property = "propertyFile", required = true)
    private File propertyFile;

    @Parameter(defaultValue = "", property = "testedVersion", required = true)
    private String testedVersion;

    /**
     * Location of the produced report.
     */
    @Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            Collection<Suite> suites = ReportParser.parse(reportDirectory);
            ScenarioMap scenarios = ScenarioParser.parse(scenarioDirectory);
            Properties properties = loadProperties();
            for (Suite suite : suites) {
                ReportGenerator generator = new ReportGenerator(outputDirectory, suite, scenarios, properties);
                generator.createReport();
            }
        } catch (MavenReportException | IOException | ReportException | ScenarioParserException e) {
            getLog().error(e);
            throw new MojoExecutionException("Error creating report ", e);
        }
    }

    private Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(Files.newReader(propertyFile, Charset.defaultCharset()));
        properties.put("version", testedVersion);
        return properties;
    }
}
