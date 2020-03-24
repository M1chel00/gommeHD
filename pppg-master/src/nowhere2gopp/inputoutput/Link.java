package nowhere2gopp.inputoutput;

import nowhere2gopp.preset.SiteSet;

import java.awt.*;

/**
 * Die Klasse stellt ein Polygon zur verfügung, mit dem der Link zwischen zwei
 * Sites gezeichnet werden kann
 * @author Jan
 */
class Link extends Polygon {
    private Point point1;
    private Point point2;
    private SiteSet siteSet;
    private static final int WIDTH = 4;

    Link(int x1, int y1, int x2, int y2, SiteSet _siteSet) {
        point1 = new Point(x1, y1);
        point2 = new Point(x2, y2);
        siteSet = _siteSet;
        calculatePoints();
    }

    /**
     * Berechnet anhand der Positionen der Sites, wie das Polygon des Links
     * aussieht
     */
    private void calculatePoints() {

        int VX = point2.x - point1.x;
        int VY = point2.y - point1.y;
        int PX = VY;
        int PY = -VX;

        double length = Math.sqrt((PX * PX)+(PY * PY));
        double NX = PX / length;
        double NY = PY / length;

        Point[] points = new Point[4];
        points[0] = new Point((int)(point1.x + NX * WIDTH),
                              (int)(point1.y + NY * WIDTH));

        points[1] = new Point((int)(point1.x - NX * WIDTH),
                              (int)(point1.y - NY * WIDTH));


        points[2] = new Point((int)(point2.x + NX * WIDTH),
                              (int)(point2.y + NY * WIDTH));

        points[3] = new Point((int)(point2.x - NX * WIDTH),
                              (int)(point2.y - NY * WIDTH));

        addPoint(points[0].x, points[0].y);
        addPoint(points[1].x, points[1].y);
        addPoint(points[3].x, points[3].y);
        addPoint(points[2].x, points[2].y);

    }

    SiteSet getSiteSet() {
        return siteSet;
    }
}
