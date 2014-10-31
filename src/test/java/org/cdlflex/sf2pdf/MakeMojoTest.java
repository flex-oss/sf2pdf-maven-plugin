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

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

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
