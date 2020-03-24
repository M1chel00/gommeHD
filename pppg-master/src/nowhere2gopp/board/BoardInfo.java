package nowhere2gopp.board;

import nowhere2gopp.preset.*;
import java.util.HashSet;
import java.util.Collection;

/**
 * Kapselung der Informationen ueber das Spielbrett.
 *
 * Dient zur Informations uebermittlung an andere Klassen
 *
 * @author jakob
 */
public class BoardInfo implements Viewer {

    /**
     * Referenz auf das Board das dieses Obejekt wiederspiegelt um auf
     * Funktionen darauf zuzugreifen.
     */
    private Board parent;

    /**
     * Groesse des Boards.
     */
    protected int size;

    /**
     * Farbe des Spielers, der gerade dran ist.
     */
    protected PlayerColor currTurn;

    /**
     * Die Phase in der sich das Spiel befindet. LinkLink oder
     * AgentLink.
     */
    protected MoveType phase;

    /**
     * Der Status des Boards. Ok, Illegal, RedWin, BlueWin oder Draw.
     */
    protected Status status;

    /**
     * Die Position des blauen Spielers.
     */
    protected Site bluePos;

    /**
     * Die Postition des roten Spielers.
     */
    protected Site redPos;

    /**
     * Die Menge aller vorhanden links.
     */
    protected Collection<SiteSet> links;

    //----------------------------------------------

    /**
     * Initialisiert das Board und setzt die Referenz auf das Board.
     *
     * @param _parent Referenz auf das Board.
     */
    public BoardInfo(Board _parent) {
        parent = _parent;
    }

    /**
     * Farbe des Spielers der gerade dran ist.
     *
     * @return Farbe des Spielers
     */
    @Override
    public PlayerColor getTurn() {
        return currTurn;
    }

    /**
     * Groesse des Boards.
     *
     * @return Groesse
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * Status des Boards.
     *
     * @return Status
     */
    @Override
    public Status getStatus() {
        return status;
    }

    /**
     * Position des angegebenen Spielers.
     *
     * @param color Farbe des Spielers.
     * @return Position des Spielers.
     */
    @Override
    public Site getAgent(final PlayerColor color) {
        return color == PlayerColor.Red ? redPos : bluePos;
    }

    /**
     * Alle verfuegbaren Links.
     *
     * @return Links
     */
    @Override
    public Collection<SiteSet> getLinks() {
        return links;
    }

    /**
     * Die Phase im dem sich das Spiel befindet.
     *
     * @return Phase
     */
    @Override
    public MoveType getPhase() {
        return phase;
    }

    /**
     * Alle Felder, die der angegebene Spieler erreichen kann. Ruft dafuer die
     * Funktion des Boards auf.
     *
     * @param player Farbe des Spielers.
     * @return Die Felder die der uebergebene Spieler erreichen kann.
     */
    @Override
    public Collection<Site> accessibleSites(PlayerColor player) {
        return parent.getAccessibleSites(player);
    }

}
