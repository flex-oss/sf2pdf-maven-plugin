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
package org.cdlflex.sf2pdf.writers.itextpdf;

import com.itextpdf.text.Paragraph;

/**
 * A class that represents an indented paragraph.
 */
public class IndentedParagraph extends Paragraph {
    private static final long serialVersionUID = 1L;

    private static final float DIST_PARA_LEFT = 25f;
    private static final float DIST_PARA_RIGHT = 25f;

    public IndentedParagraph(float leading) {
        super(leading);
        setIndentation();
    }

    public IndentedParagraph() {
        setIndentation();
    }

    private void setIndentation() {
        setIndentationLeft(DIST_PARA_LEFT);
        setIndentationRight(DIST_PARA_RIGHT);
    }
}
