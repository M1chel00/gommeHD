package nowhere2gopp.inputoutput;

import nowhere2gopp.preset.*;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * Klasse stellt ein Panel zum zeichnen bereit
 * @author Jan
 */
class GraphicsBoard extends JPanel {
    /**
     * Durchmesser des Kreises
     */
    private static final int CIRCLEDIAMETER = 40;
    /**
     * Abstand für die Y Achse
     */
    private  static final int YSPACING = (int)((double) CIRCLEDIAMETER
                                         *Math.sqrt(2)/2);
    /**
     * Größe des Spielfelds
     */
    private final int SIZE;
    /**
     * Viewer Objekt zum abfragen der Boardinformation
     */
    private Viewer boardView;
    /**
     * Objekt das die Koordinaten der Sites Managet
     */
    private SiteCoordinateManager siteCoordinateManager;
    /**
     * Objekt zum Managen des Moves des Später zurück gegeben wird
     */
    private MoveManager moveManager;
    /**
     * Konstruktor.
     * @param _boardView View Objekt des Feldes;
     * @param _graphicsIO GraphicsIO Objekt
     */
    GraphicsBoard(Viewer _boardView, GraphicsIO _graphicsIO) {
        boardView = _boardView;
        siteCoordinateManager = new SiteCoordinateManager(CIRCLEDIAMETER);
        SIZE = boardView.getSize();

        calculateCoordinates();
        moveManager = new MoveManager(siteCoordinateManager, _graphicsIO);

        addMouseListener(moveManager);

        siteCoordinateManager.addLinks(boardView.getLinks());

        int width = siteCoordinateManager.getWidth();
        int height = siteCoordinateManager.getHeight();
        setPreferredSize(new Dimension(width, height));
    }


    /**
     * Rechnet alle Koordinaten aus und legt sie im
     * Manager ab
     */
    private void calculateCoordinates() {
        //Fuellen der Coordinaten
        int xOffset = (CIRCLEDIAMETER * (SIZE/2)) + CIRCLEDIAMETER;
        int yOffset = YSPACING;
        int rowSize = ((SIZE/2) + 1);
        //Schleife fuer untere Zeilen und mittlere Zeilen
        for (int i = 0; i < (SIZE/2)+1; i++) {
            //Schleife fuer unter Spalten und Mittlere
            for (int j = 0; j < rowSize; j++) {
                siteCoordinateManager.addSite(j, i, xOffset + (j * 2 * CIRCLEDIAMETER), yOffset);
            }
            yOffset += (2*YSPACING);
            xOffset -= CIRCLEDIAMETER;
            rowSize++;
        }
        //Anlegen der Oberen Zeilen
        rowSize = ((SIZE/2) + 1);
        int symmetryAxis = siteCoordinateManager.getSiteByPosition(0,(SIZE/2)).getYPosition();
        for (int i = 0; i < (SIZE/2); i++) {
            for (int j = 0; j < rowSize; j++) {
                SiteCoordinate jiSite = siteCoordinateManager.getSiteByPosition(j, i);
                int difTosymmetryAxis = symmetryAxis - jiSite.getYPosition();
                int x = (SIZE/2) + j - i;
                int y = SIZE - 1 - i;
                int xPosition = jiSite.getXPosition();
                int yPosition = jiSite.getYPosition() + (2*difTosymmetryAxis);
                siteCoordinateManager.addSite(x, y, xPosition, yPosition);
            }
            rowSize++;
        }
    }

    /**
     * Zeichnet das Panel
     * @param g Graphics Objekt
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintLinks(g);
        paintSites(g);
        paintAccessibleSites(g);
        paintAgent(g, PlayerColor.Red);
        paintAgent(g, PlayerColor.Blue);
    }

    /**
     * Zeichnet alle Links auf das Feld
     * @param g Grafik Objekt
     */
    private void paintLinks(Graphics g) {
        g.setColor(Color.ORANGE);

        Collection<SiteSet> siteSetCollection = boardView.getLinks();
        for(SiteSet s : siteSetCollection) {
            Polygon p = siteCoordinateManager.getLinkPolygon(s);
            g.fillPolygon(p);
        }
    }

    /**
     * Zeichnet die Sites in das Graphics Objekt
     * @param g Graphics Objekt
     */
    private void paintSites(Graphics g) {
        g.setColor(Color.ORANGE);

        for(SiteCoordinate site : siteCoordinateManager) {
            Point newCircle =
                siteCoordinateManager.transfer(site.getPositionPoint());
            g.fillOval(newCircle.x, newCircle.y, CIRCLEDIAMETER,
                       CIRCLEDIAMETER);
        }
    }

    /**
     * Zeichnet alle erreichbaren Sites ein
     * @param g Graphics Objekt zum Zeichnen
     */
    private void paintAccessibleSites(Graphics g) {
        if(boardView.getPhase() != MoveType.AgentLink) {
            return;
        }
        PlayerColor color = boardView.getTurn();
        Collection<Site> accessibleSites = boardView.accessibleSites(color);

        if(color == PlayerColor.Blue) {
            g.setColor(Color.CYAN);
        } else {
            g.setColor(Color.PINK);
        }

        for(Site site : accessibleSites) {
            int column = site.getColumn();
            int row = site.getRow();
            SiteCoordinate coordinate =
                siteCoordinateManager.getSiteByPosition(column, row);
            Point newCircle = coordinate.getPositionPoint();
            newCircle = siteCoordinateManager.transfer(newCircle);
            g.fillOval(newCircle.x, newCircle.y,
                       CIRCLEDIAMETER, CIRCLEDIAMETER);
        }
    }

    /**
     * Zeichnet den Agent der angegebenen Farbe auf das Graphics Objekt
     * @param g Graphics Objekt zum Zeichnen
     * @param playerColor Farbe des zu zeichnen Agents
     */
    private void paintAgent(Graphics g, PlayerColor playerColor) {
        Site playerSite = boardView.getAgent(playerColor);
        if(playerSite == null) {
            return;
        }
        if(playerColor == PlayerColor.Blue) {
            g.setColor(Color.BLUE);
        } else {
            g.setColor(Color.RED);
        }
        int agentColumn = playerSite.getColumn();
        int agentRow = playerSite.getRow();
        SiteCoordinate agentCoordinate = siteCoordinateManager.getSiteByPosition(agentColumn,
                                         agentRow);
        int agentXPosition = agentCoordinate.getXPosition();
        int agentYPosition = agentCoordinate.getYPosition();
        Point agentPoint = siteCoordinateManager.transfer(new Point(agentXPosition,
                           agentYPosition));
        g.fillOval(agentPoint.x, agentPoint.y, CIRCLEDIAMETER,
                   CIRCLEDIAMETER);
    }

    /**
     * Gibt den Move Manager zurück
     * @return Move Manager
     */
    MoveManager getMoveManager() {
        return moveManager;
    }
}
