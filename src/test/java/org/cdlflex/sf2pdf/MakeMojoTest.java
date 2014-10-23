package org.cdlflex.sf2pdf;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.cdlflex.sf2pdf.MakeMojo;

public class MakeMojoTest extends AbstractMojoTestCase {
    /** {@inheritDoc} */
    @Override
    protected void setUp() throws Exception {
        // required
        super.setUp();

    }

    /** {@inheritDoc} */
    @Override
    protected void tearDown() throws Exception {
        // required
        super.tearDown();

    }

    /**
     * @throws Exception if any
     */
    public void testMojo() throws Exception {
        File pom = getTestFile("src/test/resources/project-to-test/pom.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());

        MakeMojo makeMojo = (MakeMojo) lookupMojo("make", pom);
        assertNotNull(makeMojo);
        makeMojo.execute();

        File generatedFilesFolder = new File("target/test-harness/project-to-test");
        assertTrue(generatedFilesFolder.exists());
        File[] files = generatedFilesFolder.listFiles();
        assertEquals(2, files.length);
    }
}
