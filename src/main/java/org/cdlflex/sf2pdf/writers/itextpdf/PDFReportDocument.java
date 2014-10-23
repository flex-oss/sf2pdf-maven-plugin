package org.cdlflex.sf2pdf.writers.itextpdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.cdlflex.sf2pdf.readers.scenario.model.Step;
import org.cdlflex.sf2pdf.readers.scenario.model.TestScenario;
import org.cdlflex.sf2pdf.readers.surefire.model.Suite;
import org.cdlflex.sf2pdf.readers.surefire.model.TestCase;
import org.cdlflex.sf2pdf.readers.surefire.model.TestStatus;
import org.cdlflex.sf2pdf.writers.ReportException;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.PngImage;

public class PDFReportDocument extends Document {
    private static final Font PARA_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
    private static final Font PARA_FONT_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
    private static final Font PARA_FONT_HEADER = FontFactory.getFont(FontFactory.HELVETICA, 18, Font.BOLD);
    private BaseFont bfHelv;
    private BaseFont bfHelvBold;

    private Image imgGreen;
    private Image imgBlue;
    private Image imgRed;

    private Image iconGreen;
    private Image iconBlue;
    private Image iconRed;
    private Image iconYellow;

    private Image imgChart;

    private static final float DIST_LEFT = 50f;

    private int pageSwitch = 25;
    private int count = 0;
    private int pageCount = 2;

    private PdfWriter writer;

    private final Suite suite;
    private final java.util.List<TestCase> sortedTestCases;
    private final Properties properties;

    public PDFReportDocument(Suite suite, Properties properties) {
        this.suite = suite;
        this.properties = properties;
        this.sortedTestCases = suite.getTestCases().getSortedList();
    }

    /**
     * Exports a report.
     * 
     * @return the report as byte array.
     * @throws ReportException an exception if an error occurs.
     */
    public byte[] export() throws ReportException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            readImages();
            writer = PdfWriter.getInstance(this, baos);
            bfHelv = BaseFont.createFont(BaseFont.HELVETICA, "Cp1252", false);
            bfHelvBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, "Cp1252", false);

            open();
            drawTitlePage();
            newPage();
            drawDetailPages();
            newPage();
            drawDetailReportPages();
            newPage();
            drawSystemInformationPage();
            close();
            return baos.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new ReportException(e);
        }
    }

    private void readImages() throws IOException {
        final String PREFIX_PATH = "/img/";
        imgGreen = getImage(PREFIX_PATH + "green.png");
        imgBlue = getImage(PREFIX_PATH + "blue.png");
        imgRed = getImage(PREFIX_PATH + "red.png");
        File fChart = (File) properties.get("chart");
        imgChart = PngImage.getImage(fChart.toURI().toURL());
        imgChart.setAbsolutePosition(350, 570);
        imgChart.scaleToFit(150f, 150f);

        iconGreen = getIcon(PREFIX_PATH + "icon_green.png");
        iconBlue = getIcon(PREFIX_PATH + "icon_blue.png");
        iconRed = getIcon(PREFIX_PATH + "icon_red.png");
        iconYellow = getIcon(PREFIX_PATH + "icon_yellow.png");
    }

    private Image getImage(String path) throws IOException {
        Image image = PngImage.getImage(getResourceStream(path));
        image.setAbsolutePosition(35, 715);
        return image;
    }

    private Image getIcon(String path) throws IOException {
        Image image = PngImage.getImage(getResourceStream(path));
        image.scaleToFit(10f, 10f);
        return image;
    }

    private InputStream getResourceStream(String path) {
        return getClass().getResourceAsStream(path);
    }

    private void generateShortDetailPage() throws DocumentException {
        addNotWorkingTests();
        addAllTests();
    }

    private void addAllTests() throws DocumentException {
        Paragraph p2 = new IndentedParagraph();
        p2.add(new Chunk("All Tests", PARA_FONT_HEADER));

        float[] colsWidth1 = { 0.2f, 0.3f, 3.5f, 1.8f, 0.8f };
        PdfPTable tableAll = new PdfPTable(colsWidth1);
        tableAll.setWidthPercentage(90f);
        tableAll.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);

        for (TestCase result : sortedTestCases) {
            if (result.getTestStatus().equals(TestStatus.ERROR)) {
                tableAll.addCell(initImageCell(iconRed));
                count++;
            } else if (result.getTestStatus().equals(TestStatus.FAILURE)) {
                tableAll.addCell(initImageCell(iconBlue));
                count++;
            } else if (result.getTestStatus().equals(TestStatus.SKIP)) {
                tableAll.addCell(initImageCell(iconYellow));
                count++;
            } else if (result.getTestStatus().equals(TestStatus.PASS)) {
                tableAll.addCell(initImageCell(iconGreen));
                count++;
            }
            TestScenario testScenario = result.getScenario();
            if (testScenario == null) {
                addErrorMessageCells(tableAll);
            } else {
                tableAll.addCell(initCell(result.getScenario().getScenarioNumber().toString(), PARA_FONT_NORMAL));
                tableAll.addCell(initCell(result.getScenario().getName(), PARA_FONT_NORMAL));
                tableAll.addCell(initCell(result.getScenario().getTestCategory().name(), PARA_FONT_NORMAL));
            }
            tableAll.addCell(initCell(result.getNiceDuration(), PARA_FONT_NORMAL));

            if (count == pageSwitch) {
                makeLines(1);
                add(p2);
                makeLines(1);
                add(tableAll);
                newPage();
                drawHeader();
                drawFooter();

                pageSwitch = 40;
                count = 0;
                tableAll = new PdfPTable(colsWidth1);
                tableAll.setWidthPercentage(90f);
                tableAll.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            }
        }

        makeLines(5);
        add(tableAll);
    }

    private boolean addNotWorkingTests() throws DocumentException {
        boolean pageSwitched = false;
        Paragraph p = new IndentedParagraph();
        p.add(new Chunk("Not Working", PARA_FONT_HEADER));

        float[] colsWidth = { 0.3f, 0.5f, 4.1f, 2.4f, 1f, 2f };
        PdfPTable tableNotPass = new PdfPTable(colsWidth);
        tableNotPass.setWidthPercentage(90f);
        tableNotPass.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);

        for (int i = 0; i < 3; i++) {
            for (TestCase result : sortedTestCases) {
                if (i == 0 && result.getTestStatus().equals(TestStatus.ERROR)) {
                    tableNotPass.addCell(initImageCell(iconRed));
                    count++;
                } else if (i == 1 && result.getTestStatus().equals(TestStatus.FAILURE)) {
                    tableNotPass.addCell(initImageCell(iconBlue));
                    count++;
                } else if (i == 2 && result.getTestStatus().equals(TestStatus.SKIP)) {
                    tableNotPass.addCell(initImageCell(iconYellow));
                    count++;
                } else {
                    continue;
                }
                TestScenario testScenario = result.getScenario();
                if (testScenario == null) {
                    addErrorMessageCells(tableNotPass);
                } else {
                    tableNotPass.addCell(initCell(testScenario.getScenarioNumber().toString(), PARA_FONT_NORMAL));
                    tableNotPass.addCell(initCell(testScenario.getName(), PARA_FONT_NORMAL));
                    tableNotPass.addCell(initCell(testScenario.getTestCategory().name(), PARA_FONT_NORMAL));
                }
                tableNotPass.addCell(initCell(result.getNiceDuration(), PARA_FONT_NORMAL));
                tableNotPass.addCell(initCell("no short comment!", PARA_FONT_NORMAL));
                if (count == pageSwitch) {
                    newPage();
                    drawHeader();
                    drawFooter();
                    pageSwitch = 50;
                    count = 0;
                    pageSwitched = true;
                }
            }
        }
        makeLines(4);
        add(p);
        makeLines(1);
        add(tableNotPass);
        return pageSwitched;
    }

    private void addErrorMessageCells(PdfPTable pdfTable) {
        pdfTable.addCell(initCell("0", PARA_FONT_NORMAL));
        pdfTable.addCell(initCell("check scenario.xml for missing info", PARA_FONT_NORMAL));
        pdfTable.addCell(initCell("!ERROR!", PARA_FONT_NORMAL));
    }

    private void drawTitlePage() throws DocumentException {
        PdfContentByte cb = writer.getDirectContent();
        cb.beginText();
        cb.setFontAndSize(this.bfHelvBold, 36f);
        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, properties.getProperty("headline1"), 290, 550, 0);
        cb.setFontAndSize(this.bfHelvBold, 24f);
        cb.showTextAligned(PdfContentByte.ALIGN_CENTER,
                MessageFormat.format(properties.getProperty("headline2"), properties.getProperty("version")), 290,
                510, 0);
        drawFrontPageInfoField(properties.getProperty("contact"), properties.getProperty("contactText"), cb, 410);
        drawFrontPageInfoField(properties.getProperty("email1"), properties.getProperty("email1Text"), cb, 360);
        drawFrontPageInfoField(properties.getProperty("email2"), properties.getProperty("email2Text"), cb, 310);
        cb.setFontAndSize(this.bfHelv, 10f);
        DateFormat fmt = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, "Created: " + fmt.format(new Date()), 580, 10, 0);
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, properties.getProperty("footnote1"), 20, 10, 0);
        cb.endText();
        newPage();
    }

    private void drawFrontPageInfoField(String title, String text, PdfContentByte cb, Integer startingHeight) {
        cb.setFontAndSize(this.bfHelvBold, 18f);
        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, title, 290, startingHeight, 0);
        cb.setFontAndSize(this.bfHelv, 14f);
        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, text, 290, startingHeight - 20, 0);
    }

    private void drawDetailPages() throws DocumentException, IOException {
        generateReportSummaryPage();
        generateShortDetailPage();
    }

    private void drawSystemInformationPage() throws DocumentException {
        drawHeader();
        drawFooter();

        makeLines(4);
        Paragraph p = new IndentedParagraph();
        p.add(new Chunk("Test Environment Specification", PARA_FONT_HEADER));
        add(p);

        makeLines(1);
        addTextParagraph("Java Version: " + getSuiteProp("java.version"));
        makeLines(1);
        addTextParagraph("OS: " + getSuiteProp("os.name") + "; " + getSuiteProp("os.version") + "; "
            + getSuiteProp("os.arch"));
        makeLines(1);
        Runtime runtime = Runtime.getRuntime();
        addTextParagraph("Max Memory (bytes): " + runtime.maxMemory());
        makeLines(1);
        addTextParagraph("Total Memory (bytes): " + runtime.totalMemory());
        makeLines(1);
        addTextParagraph("Free Memory (bytes): " + runtime.freeMemory());
        makeLines(1);
        addTextParagraph("Available Processors (threads): " + runtime.availableProcessors());
        makeLines(1);
        addTextParagraph("HD total space (bytes): " + new File("/").getTotalSpace());
        makeLines(1);
        addTextParagraph("HD free space (bytes): " + new File("/").getFreeSpace());
    }

    private void addTextParagraph(String text) throws DocumentException {
        Paragraph p = new Paragraph();
        p.add(new Chunk(text));
        add(p);
    }

    private String getSuiteProp(String property) {
        return suite.getProperties().get(property);
    }

    private void generateReportSummaryPage() throws IOException, DocumentException {
        drawHeader();
        drawFooter();

        makeLines(4);
        Paragraph p = new IndentedParagraph();
        p.add(new Chunk("Management Summary", PARA_FONT_HEADER));
        p.add(imgChart);
        add(p);

        makeLines(1);
        Paragraph p2 = new IndentedParagraph();
        p2.add(new Chunk("Details: ", PARA_FONT));
        add(p2);
        makeLines(1);

        float[] colsWidth = { 1f, 2f }; // Code 1
        PdfPTable table = new PdfPTable(colsWidth);

        table.setTotalWidth(250f);

        table.setHorizontalAlignment(Element.ALIGN_CENTER);// Code 3

        table.addCell(initCell("Tests: ", PARA_FONT));
        table.addCell(initCell(Integer.toString(suite.getNumberOfTests()), PARA_FONT_NORMAL));
        table.addCell(initCell("Passed: ", PARA_FONT));
        table.addCell(initCell(suite.getNumberOfPassed() + " ( " + suite.getPassedPercentage() + "% )",
                PARA_FONT_NORMAL));
        table.addCell(initCell("Failed: ", PARA_FONT));
        table.addCell(initCell(suite.getNumberOfFailures() + " ( " + suite.getFailedPercentage() + "% )",
                PARA_FONT_NORMAL));
        table.addCell(initCell("Error: ", PARA_FONT));
        table.addCell(initCell(suite.getNumberOfErrors() + " ( " + suite.getErrorPercentage() + "% )",
                PARA_FONT_NORMAL));
        table.addCell(initCell("Skipped: ", PARA_FONT));
        table.addCell(initCell(suite.getNumberOfSkipped() + " ( " + suite.getSkippedPercentage() + "% )",
                PARA_FONT_NORMAL));
        table.addCell(initCell("Total Time: ", PARA_FONT));
        table.addCell(initCell(suite.getTotalDurationFormatted() + " [hh:mm:ss]", PARA_FONT_NORMAL));

        add(table);
    }

    private PdfPCell initCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private PdfPCell initImageCell(Image img) {
        PdfPCell cell = new PdfPCell(img);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private void drawFooter() {
        PdfContentByte cb = writer.getDirectContent();
        cb.rectangle(DIST_LEFT - 10, 25, 600 - 2 * DIST_LEFT + 10, 50);
        cb.stroke();
        cb.beginText();
        setNormalFont(cb);
        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "Page " + pageCount++, 290, 32, 0);
        cb.endText();
    }

    private void drawHeader() {
        PdfContentByte cb = writer.getDirectContent();
        cb.rectangle(DIST_LEFT - 10, 750, 600 - 2 * DIST_LEFT + 10, 50);
        cb.stroke();
        cb.beginText();
        setChapterFont(cb);
        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "Test Report, Version " + properties.getProperty("version"),
                290, 767, 0);
        cb.endText();
    }

    private void drawDetailReportPages() throws DocumentException {
        for (TestCase testCase : sortedTestCases) {
            drawHeader();
            drawFooter();
            Paragraph p = new IndentedParagraph();
            TestScenario testScenario = testCase.getScenario();
            if (testScenario == null) {
                continue;
            }
            p.add(new Chunk(testScenario.getScenarioNumber() + ". " + testScenario.getName(), PARA_FONT_HEADER));
            if (testCase.getTestStatus().equals(TestStatus.PASS)) {
                if (imgGreen != null) {
                    p.add(imgGreen);
                }
            } else if (testCase.getTestStatus().equals(TestStatus.FAILURE)) {
                if (imgBlue != null) {
                    p.add(imgBlue);
                }
            } else if (testCase.getTestStatus().equals(TestStatus.ERROR)) {
                if (imgRed != null) {
                    p.add(imgRed);
                }
            }
            Paragraph p2 = new IndentedParagraph();
            p2.add(new Chunk("Preconditions: ", PARA_FONT));
            Paragraph p3 = new IndentedParagraph();
            p3.add(new Chunk("Steps:", PARA_FONT));
            Paragraph p4 = new IndentedParagraph();
            p4.add(new Chunk("Results", PARA_FONT));

            Paragraph pPreconditions = new IndentedParagraph(3.0f);
            List lPreconditions = new ReportList(false, 30);
            for (String s : testScenario.getPreconditions()) {
                ListItem li = new ListItem(s);
                li.setFont(PARA_FONT);
                lPreconditions.add(li);
            }
            pPreconditions.add(lPreconditions);

            Paragraph pSteps = new IndentedParagraph(3.0f);
            List lSteps = new ReportList(false, 30);
            for (Step s : testScenario.getSteps()) {
                ListItem li = new ListItem(s.getDescription());
                li.setFont(PARA_FONT);
                lSteps.add(li);
            }
            pSteps.add(lSteps);

            Paragraph pResults = new IndentedParagraph(3.0f);
            List lResults = new ReportList(false, 30);
            for (String s : testScenario.getExpectedResults()) {
                ListItem li = new ListItem(s);
                li.setFont(PARA_FONT);
                lResults.add(li);
            }
            pResults.add(lResults);

            // add to document
            makeLines(4);
            add(p);
            makeLines(1);
            add(p2);
            add(pPreconditions);
            makeLines(1);
            add(p3);
            add(pSteps);
            makeLines(1);
            add(p4);
            add(pResults);
            makeLines(1);
            newPage();
        }
    }

    private void makeLines(int x) throws DocumentException {
        for (int i = 0; i < x; i++) {
            add(new Paragraph(" "));
        }
    }

    private void setNormalFont(PdfContentByte cb) {
        cb.setFontAndSize(bfHelv, 10f);
    }

    private void setChapterFont(PdfContentByte cb) {
        cb.setFontAndSize(bfHelvBold, 22f);
    }
}
