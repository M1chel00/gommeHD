package nowhere2gopp.inputoutput;

import nowhere2gopp.preset.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**Klasse um den Move zu erstellen, der abgefragt wrid
 * @author Jan
 */
class MoveManager implements MouseListener {
    /**
     * Manager der Site Coordinates
     */
    private SiteCoordinateManager siteCoordinateManager;
    /**
     * Zug der erstellt wird
     */
    private Move move;
    /**
     * Grafik Objekt
     */
    private GraphicsIO graphicsIO;
    /**
     * Objekt zum Synchronisieren der Threads
     */
    private Object monitor;
    /**
     * Typ des Moves
     */
    private MoveType type;
    /**
     * Array mit ausgewählten Sites
     */
    private Site[] sites;
    /**
     * Zählvariable für Eingaben
     */
    private int countSites;


    /**
     * Konstruktor
     * @param _manager Manager für SitesCoordinate
     * @param _graphicsIO Grafik Objekt
     */
    MoveManager(SiteCoordinateManager _manager, GraphicsIO _graphicsIO) {
        siteCoordinateManager = _manager;
        graphicsIO = _graphicsIO;
        monitor = new Object();
        type = null;
    }

    /**
     * Es wird eine Kopie des Aktuellen Moves erstellt.
     * Anschließen wird die Refernez imObjekt auf null gesetzt
     * @return Eingegebener Move
     */
    public Move getMove() {
        if(move == null) {
            return null;
        }
        Move returnValue = cloneMove(move);
        move = null;
        type = null;
        countSites = 0;
        graphicsIO.setMove("Currently not your turn");
        return returnValue;
    }

    /**
     * Erstellt ein Move Objekt mit den Selben eigenschaften des
     * Uebergeben Objekts
     * @param value Zu klonenes Objekt
     * @return Klon des Objektes
     */
    private Move cloneMove(Move value) {
        MoveType type = value.getType();
        if(type == MoveType.End || type == MoveType.Surrender) {
            return new Move(type);
        } else if(type == MoveType.LinkLink) {
            return new Move(value.getOneLink(), value.getOtherLink());
        } else {
            return new Move(value.getAgent(), value.getLink());
        }

    }

    /**
     * Erstellt einen Surrender Move und Weckt den Anfragenen Thread
     */
    void surrenderButtonClicked() {
        move = new Move(MoveType.Surrender);
        graphicsIO.setEnableButtons(false);
        synchronized (monitor) {
            monitor.notifyAll();
        }
    }

    /**
     * Wenn noch kein Typ festgelegt wurde, wird der Typ Linklink angelegt
     */
    void linkLinkMoveRequested() {
        if(type != null) {
            return;
        }
        moveRequested();
        type = MoveType.LinkLink;

    }

    /**
     * Wenn noch kein Typ festgelegt wurde, wird der Typ Agentlink angelegt
     */
    void agentLinkMoveRequested() {
        if(type != null) {
            return;
        }
        moveRequested();
        type = MoveType.AgentLink;
    }

    /**
     * Ein move Wurde angefragt.
     * Der Button wird freigegeben, das Array angelegt und die Zählvariable
     * zurückgesetzt
     */
    private void moveRequested() {
        graphicsIO.setEnableButtons(true);
        sites = new Site[4];
        countSites = 0;
    }

    /**
     * Mous klick event.
     * Wenn ein Klick erwartet wird und er auf einer Site liegt, dann wird die
     * Site gespeichert
     * @param e Event Objekt
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if(type == null || countSites == 4) {
            return;
        }

        Point newPoint = new Point(e.getX(), e.getY());
        newPoint = siteCoordinateManager.transfer(newPoint);
        SiteCoordinate siteCoordinate;
        SiteSet siteSet = null;
        siteCoordinate = siteCoordinateManager.getCircleContainingPosition
                         (newPoint.x, newPoint.y);

        if(siteCoordinate == null) {
            siteSet = siteCoordinateManager.getLinkContainingPosition(e.getX(),
                      e.getY());
        }


        if(siteCoordinate != null && countSites == 1
                && type == MoveType.AgentLink) {
            Site siteFromCoordinates = new Site(siteCoordinate.getColumn(),
                                                siteCoordinate.getRow());

            addSite(siteFromCoordinates);
            return;
        }
        if(countSites % 2 == 1 || siteSet == null) {
            return;
        }
        if(countSites == 2 && type == MoveType.AgentLink
                || type == MoveType.LinkLink) {
            addSite(siteSet.getFirst());
            addSite(siteSet.getSecond());
        }
    }

    void addSite(Site site) {
        String sitesString;
        if (type == MoveType.LinkLink) {
            if(countSites == 0) {
                sitesString = site.getColumn() + " "
                              + site.getRow();
                graphicsIO.setMove("Selected Sites: ");
                graphicsIO.addToMove(sitesString);
            } else {
                sitesString = site.getColumn() + " "
                              + site.getRow();
                graphicsIO.addToMove(sitesString);
            }
        } else {
            if(countSites == 1) {
                sitesString = site.getColumn() + " "
                              + site.getRow();
                graphicsIO.setMove("Selected Sites: ");
                graphicsIO.addToMove(sitesString);
            } else if(countSites > 1) {
                sitesString = site.getColumn() + " "
                              + site.getRow();
                graphicsIO.addToMove(sitesString);
            }
        }

        sites[countSites] = site;
        countSites++;
        if(countSites > 3) {
            allSitesRead();
        }
    }

    /**
     * Wenn alle nötigen Sites ausgewählt worden sind, dann wird ein Move
     * angelegt und der anfragende Thread wird geweckt
     */
    private void allSitesRead() {
        if(type == MoveType.LinkLink) {
            SiteSet[] siteSets = new SiteSet[2];
            siteSets[0] = new SiteSet(sites[0], sites[1]);
            siteSets[1] = new SiteSet(sites[2], sites[3]);
            move = new Move(siteSets[0], siteSets[1]);
        } else if (type == MoveType.AgentLink) {
            SiteTuple siteTuple = new SiteTuple(sites[0], sites[1]);
            SiteSet siteSet = new SiteSet(sites[2], sites[3]);
            move = new Move(siteTuple, siteSet);
        }
        graphicsIO.setEnableButtons(false);
        synchronized (monitor) {
            monitor.notifyAll();
        }
    }

    Object getMonitor() {
        return monitor;
    }

    /**
     * Nicht implementiert.
     * Wird nur für das Interface benötigt
     * @param e Event Objekt
     */
    @Override
    public void mousePressed(MouseEvent e) {

    }

    /**
     * Nicht implementiert.
     * Wird nur für das Interface benötigt
     * @param e Event Objekt
     */
    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Nicht implementiert.
     * Wird nur für das Interface benötigt
     * @param e Event Objekt
     */
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * Nicht implementiert.
     * Wird nur für das Interface benötigt
     * @param e Event Objekt
     */
    @Override
    public void mouseExited(MouseEvent e) {

    }
}
