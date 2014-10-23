package org.cdlflex.sf2pdf.writers;

import java.awt.Paint;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.cdlflex.sf2pdf.readers.scenario.model.ScenarioMap;
import org.cdlflex.sf2pdf.readers.scenario.model.TestScenario;
import org.cdlflex.sf2pdf.readers.surefire.model.Suite;
import org.cdlflex.sf2pdf.readers.surefire.model.TestCase;
import org.cdlflex.sf2pdf.readers.surefire.model.TestCaseMap;
import org.cdlflex.sf2pdf.readers.surefire.model.TestStatus;
import org.cdlflex.sf2pdf.util.Format;
import org.cdlflex.sf2pdf.writers.itextpdf.PDFReportDocument;
import org.krysalis.jcharts.chartData.ChartDataException;
import org.krysalis.jcharts.chartData.PieChartDataSet;
import org.krysalis.jcharts.encoders.PNGEncoder;
import org.krysalis.jcharts.nonAxisChart.PieChart2D;
import org.krysalis.jcharts.properties.ChartProperties;
import org.krysalis.jcharts.properties.LegendProperties;
import org.krysalis.jcharts.properties.PieChart2DProperties;
import org.krysalis.jcharts.properties.PropertyException;

import com.google.common.io.Files;

/**
 * Matches scenarios with suites, creates charts and the pdf report.
 */
public class ReportGenerator {

    private final Suite suite;
    private final File outputDirectory;
    private PieChartDataSet chartDataSet;
    private final Properties properties;

    public ReportGenerator(File outputDirectory, Suite suite, ScenarioMap scenarios, Properties properties) {
        this.outputDirectory = outputDirectory;
        this.suite = suite;
        this.properties = properties;
        addScenariosToSuite(suite, scenarios);
    }

    public void createReport() throws ReportException {
        String dateString = Format.date(new Date());
        String documentName = dateString + " - Acceptance Test Report V" + properties.getProperty("version");
        File output = new File(outputDirectory, documentName + ".pdf");
        try {
            Files.createParentDirs(output);
            createCharts(suite, outputDirectory);
            PDFReportDocument ae = new PDFReportDocument(suite, properties);
            Files.write(ae.export(), output);
        } catch (IOException e) {
            throw new ReportException(e);
        }
    }

    private void createCharts(Suite suite, File chartFolder) throws ReportException {
        double[] data =
            { suite.getPassedPercentage(), suite.getFailedPercentage(), suite.getErrorPercentage(),
                suite.getSkippedPercentage() };
        String[] labels = { "Passed", "Failed", "Error", "Skipped" };
        Paint[] paints =
            { TestStatus.PASS.getColor(), TestStatus.FAILURE.getColor(), TestStatus.ERROR.getColor(),
                TestStatus.SKIP.getColor() };
        PieChart2DProperties chartProperties = new PieChart2DProperties();
        try {
            chartDataSet = new PieChartDataSet("Results", data, labels, paints, chartProperties);
            File chartFile = createChart("chart", 200);
            properties.put("chart", chartFile);
        } catch (ChartDataException e) {
            throw new ReportException(e);
        }
    }

    private File createChart(String name, int size) throws ReportException {
        PieChart2D chart = new PieChart2D(chartDataSet, new LegendProperties(), new ChartProperties(), size, size);
        String chartUrl = outputDirectory.getAbsolutePath() + File.separator + name + suite.getName() + ".png";
        File chartFile = new File(chartUrl);
        try (FileOutputStream fos = new FileOutputStream(chartFile)) {
            PNGEncoder.encode(chart, fos);
        } catch (IOException | ChartDataException | PropertyException e) {
            throw new ReportException(e);
        }
        return chartFile;
    }

    private void addScenariosToSuite(Suite suite, ScenarioMap scenarios) {
        TestCaseMap map = suite.getTestCases();
        for (List<TestCase> testCases : map.values()) {
            for (TestCase testCase : testCases) {
                String key = createScenarioKey(testCase);
                TestScenario scenario = scenarios.get(key);
                if (scenario == null) {
                    System.out.println("No matching scenario found for Test " + key);
                } else {
                    testCase.setScenario(scenario);
                }
            }
        }
    }

    private String createScenarioKey(TestCase testCase) {
        return testCase.getClassName() + "." + testCase.getName();
    }
}
