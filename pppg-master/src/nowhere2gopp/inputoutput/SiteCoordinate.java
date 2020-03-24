package nowhere2gopp.inputoutput;

import nowhere2gopp.preset.Site;

import java.awt.*;

/**
 * Die Klasse Speicher die Logische Position (row, column)
 * und die Physische Position (xPosition, yPosition)
 * @author Jan
 */
class SiteCoordinate {

    /**
     * Logische Position der Spalte
     */
    private int column;
    /**
     * Logische Position der Zeile
     */
    private int row;
    /**
     * X Wert der Position
     */
    private int xPosition;
    /**
     * Y Wert der Position
     */
    private int yPosition;

    /**
     * Konstruktor
     * @param _column Spalte der Site
     * @param _row Rheie der Site
     * @param _xPosition X Position der Site
     * @param _yPosition Y Position der Site
     */
    SiteCoordinate(int _column, int _row, int _xPosition, int _yPosition) {
        column = _column;
        row = _row;
        xPosition = _xPosition;
        yPosition = _yPosition;
    }

    int getColumn() {
        return column;
    }

    int getRow() {
        return row;
    }

    int getXPosition() {
        return xPosition;
    }

    int getYPosition() {
        return yPosition;
    }

    /**
     * Gibt einen Point zurueck
     * @return Point der die Position enthaelt
     */
    Point getPositionPoint() {
        return new Point(xPosition, yPosition);
    }
}
