package nowhere2gopp.inputoutput;

import nowhere2gopp.preset.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *	Verwaltet alle Sites
 *  @author Jan
 */
class SiteCoordinateManager implements Iterable<SiteCoordinate> {
    private ArrayList<Link> linkList;
    /**
     * Speichert alle SiteCoordinate Objekte ab
     */
    private ArrayList<SiteCoordinate> siteCoordinatList;
    /**
     * Speichert die Höhe der Sites
     */
    private int height = -1;
    /**
     * Speichert die Weite der Sites
     */
    private int width = -1;
    /**
     * Der Durchmesser des Kreises.
     * Wird gebraucht um die Höhe auszurechnen
     */
    private final int CIRCLEDIAMETER;

    @SuppressWarnings("SameParameterValue")
    SiteCoordinateManager(int circleDiameter) {
        siteCoordinatList = new ArrayList<>();
        linkList = new ArrayList<>();
        CIRCLEDIAMETER = circleDiameter;
    }

    /**
     * Fügt eine neue Site
     * @param column Spalte der Site
     * @param row Zeile der Site
     * @param xPosition X Position der Site
     * @param yPosition Y Position der Site
     */
    void addSite(int column, int row, int xPosition, int yPosition) {
        siteCoordinatList.add(new SiteCoordinate(column, row, xPosition,
                              yPosition));

        if(height < yPosition + CIRCLEDIAMETER) {
            height = yPosition + CIRCLEDIAMETER;
        }
        if (width < xPosition + CIRCLEDIAMETER) {
            width = xPosition + CIRCLEDIAMETER;
        }
    }

    /**
     * Gitb die Site zurück in die Geklickt wurde
     * @param xp X Position des Klicks
     * @param yp Y Position des Klicks
     * @return Site in die geklickt wurde. Sollte die Position nicht in einer
     * Site liegen, wird null zurück gegeben
     */
    SiteCoordinate getCircleContainingPosition(int xp, int yp) {
        for(SiteCoordinate s : this) {
            Point center = getCircleCenter(s.getColumn(), s.getRow());
            int xc = center.x;
            int yc = center.y;
            int d = ((xp-xc)*(xp-xc)) + ((yp-yc)*(yp-yc));
            int r = (CIRCLEDIAMETER/2) * (CIRCLEDIAMETER/2);

            if(d <= r) {
                return s;
            }
        }
        return null;
    }

    SiteSet getLinkContainingPosition(int xp, int yp) {
        for(Link l : linkList) {
            if(l.contains(xp, yp)) {
                return l.getSiteSet();
            }
        }
        return null;
    }

    @Override
    public Iterator<SiteCoordinate> iterator() {
        return siteCoordinatList.iterator();
    }

    int getHeight() {
        return height;
    }

    int getWidth() {
        return width;
    }

    /**
     * Gibt ein SiteCoordinate Objekt der Passenden Position zurück.
     * Falls nicht vorhanden, wird null zurückgegeben.
     * @param _column X Position der gesuchten Site
     * @param _row Y Position der gesuchten Site
     * @return Gesuchte Site oder null
     */
    SiteCoordinate getSiteByPosition(int _column, int _row) {
        for(SiteCoordinate site : this) {
            if(site.getColumn() == _column && site.getRow() == _row) {
                return site;
            }
        }
        return null;
    }

    /**
     * Errechnet von Allen SiteSets das nötige Polynom um die Link zu zeichen.
     * Die Polynome werden anschließen abgespeichert
     * @param collection Links
     */
    void addLinks(Collection<SiteSet> collection) {
        for (SiteSet s : collection) {

            Site a = s.getFirst();
            Site b = s.getSecond();

            Point point1 = getCircleCenter(a.getColumn(), a.getRow());
            Point point2 = getCircleCenter(b.getColumn(), b.getRow());

            point1 = transfer(point1);
            point2 = transfer(point2);

            Link returnValue = new Link(point1.x, point1.y, point2.x, point2.y,
                                        s);

            linkList.add(returnValue);
        }
    }

    /**
     * Gitb das Polynom zurück, das die Sites verbindet
     * @param s SiteSet
     * @return Polygon
     */
    Polygon getLinkPolygon(SiteSet s) {
        for(Link l : linkList) {
            if(l.getSiteSet().equals(s)) {
                return l;
            }
        }
        return null;
    }

    /**
     * Wandelt einen Punkt so um, das er in einem Koordinatenkreutz
     * mit Ursprung oben links angezeigt werden kann
     * @param value Punkt zum Umwandeln
     * @return neuer Punkt
     */
    Point transfer(Point value) {
        int newYValue =getHeight() - value.y - CIRCLEDIAMETER;
        return new Point(value.x, newYValue);
    }

    /**
     * Gibt die Mittlere Position des Kreises an
     * @param _x Logische x Position des Kreises
     * @param _y Logische y Position des Kreises
     * @return Physische Mitte des Kreises
     */
    private Point getCircleCenter(int _x, int _y) {
        SiteCoordinate coordinate = getSiteByPosition(_x, _y);
        int x = coordinate.getXPosition();
        int y = coordinate.getYPosition();
        Point returnValue = new Point(x, y);
        returnValue.x += (CIRCLEDIAMETER/2);
        returnValue.y -= (CIRCLEDIAMETER/2);
        return returnValue;

    }
}
