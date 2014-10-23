package org.cdlflex.sf2pdf.readers.surefire.model;

import java.awt.Color;

public enum TestStatus {
    SKIP(Color.YELLOW),
    ERROR(Color.RED),
    FAILURE(Color.BLUE),
    PASS(Color.GREEN);

    private Color col;

    TestStatus(Color col) {
        this.col = col;
    }

    public Color getColor() {
        return col;
    }

    public int invOrdinal() {
        return TestStatus.values().length - 1 - this.ordinal();
    }
}
