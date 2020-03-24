package nowhere2gopp.board;

import nowhere2gopp.preset.*;
import java.util.Collection;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * Implementiert funktionalitaet des Spielbretts. Speichert dazu die Sites,
 * Links, Positionen der Spieler und Zustand des aktuellen Spiels.
 * Ueberprueft Spielzuege auf Richtigkeit.
 *
 *
 * @author jakob
 */
public class Board implements Playable {

    /**
     * max fuer die Groesse des Spielfelds
     */
    private final int MAX_SIZE = 11;
    /**
     * min fuer die Groesse des Spielfelds
     */
    private final int MIN_SIZE = 3;

    /**
     * Groesse des Spielfelds n=2k+1
     */
    private int size_n;
    /**
     * Groesse des Spielfelds
     */
    private int size_k;

    /**
     * speichert die Referenz auf das BoardInfo Objekt.
     */
    private BoardInfo info;

    /**
     * speichert alle sites nach der Ordnung auf dem Brett
     */
    private Site[][] sites;
    /**
     * Menger aller Sites, fuer einfachen contains Zugriff. Wird in der
     * getNeighbours Methode gebraucht um nicht immer auf die bounds des Arrays
     * pruefen zu muessen.
     */
    private ArrayList<Site> siteCol= new ArrayList<Site>();
    /**
     * Menge aller vorhandenen Links
     */
    private ArrayList<SiteSet> links = new ArrayList<SiteSet>();

    /**
     * Positionen des roten Spielers
     */
    private Site redPos;
    /**
     * Positionen des blauen Spielers
     */
    private Site bluePos;

    /**
     * Speichert welcher Spieler als naechstes dran ist
     */
    private PlayerColor currTurn;

    /**
     * Speichert Status des laufenden Spiels
     */
    private Status status;

    /**
     * welche art von Zug wird erwarted
     */
    private MoveType phase;

    /**
     * Anzahl an Zuegen speichern um phase zu bestimmen
     */
    private int moveCount;

    /**
     * letzten beiden moves fuer die undo funktion speichern
     */
    private Move[] moveBuffer;

    //-------------------------------------------------------------------------


    /**
     * Initialisiert das Spielbrett mit der uebergenen groesse.
     * Initialiesung der Sites, der Links und des info Objekts.
     *
     * @param size goesse des neuen Spielbretts
     */
    public Board(int size) {

        // Groessen Variablen setzen
        size_k = size;

        size_n = 2 * size_k + 1;

        // Move Counter und Phase setzen
        moveCount = 0;
        phase = MoveType.LinkLink;

        // Move Buffer initalisieren
        moveBuffer = new Move[2];

        // den ersten Zug hat immer Rot
        currTurn = PlayerColor.Red;

        //spalten initialisieren
        sites = new Site[size_n][];

        // Zeilen initialisieren
        int mid = (size_n+1)/2;
        int row = mid;
        int add = 1;

        for (int i = 0; i < sites.length; i++) {
            sites[i] = new Site[row];
            if (row < sites.length) {
                row++;
            }
        }


        // Array mit Sites fuellen
        // bzw das dreieck unten rechts mit NULL
        //
        // Um die Zeilen/Spalten Struktur korrekt zu initialisieren soll das
        // Array so aussehen bsp size_n = 7 (mit O = ein Site Objekt, - = null)
        // 6    OOOO
        // 5   OOOOO
        // 4  OOOOOO
        // 3 OOOOOOO
        // 2 OOOOOO-
        // 1 OOOOO--
        // 0 OOOO---
        //   0123456
        //
        // benutzung des Arrays: sites[column][row]

        // Die Sites bis vor der Reihe wo das leere Dreieck anfaengt einfuegen
        for (int i = 0; i < mid; i++) {
            for (int j = 0; j < sites[i].length; j++) {
                Site tmp = new Site(i, j);
                sites[i][j] = tmp;
                siteCol.add(tmp);
            }
        }
        // Die Sites und null einfuegen in den Reihen wo das Dreieck ist
        for (int i = 0; i < mid-1; i++) {
            // null's einfuegen
            for (int j = 0; j < i+1; j++) {
                sites[mid+i][j] = null;
            }
            // Rest der Reihe mit Sites auffuellen
            for (int j = i+1; j < size_n; j++) {
                Site tmp = new Site(mid+i, j);
                sites[mid+i][j] = tmp;
                siteCol.add(tmp);
            }
        }

        // alle benoetigten Links (SiteSets) in das ArrayList einfuegen

        // nacheinander alle drei richtungen an links machen

        // 1. richtung Spalten
        int count = 0;
        for (int i = 0; i < sites.length; i++) {
            for (int k = 0; k < sites[i].length - 1; k++) {
                if (sites[i][k] == null) {
                    continue;
                }
                links.add(new SiteSet(sites[i][k], sites[i][k+1]));
                count++;
            }
        }
        // 2. richtung Zeilen
        for (int i = 0; i < sites.length - 1; i++) {
            for (int k = 0; k < sites[i].length; k++) {
                if (sites[i][k] == null || sites[i+1][k] == null) {
                    continue;
                }
                links.add(new SiteSet(sites[i][k], sites[i+1][k]));
                count++;
            }
        }
        // 3. richtung Diagonal
        for (int i = 0; i < sites.length - 1; i++) {
            for (int k = 0; k < sites[i].length; k++) {
                if (k+1 >= sites[i+1].length || sites[i][k] == null || sites[i+1][k+1] == null) {
                    continue;
                }
                links.add(new SiteSet(sites[i][k], sites[i+1][k+1]));
                count++;
            }
        }

        // info objekt initialiesern

        info = new BoardInfo(this);

        status = Status.Ok;
        info.size = size_n;
        updateInfo();
    }

    //------------------------------------------------------------------
    // public methoden

    /**
     * Gibt ein Viewer (BoardInfo) Objekt zurueck, welches anderen Klassen
     * Zugriff auf die Informationen des Bretts bereitstellt.
     *
     * @return Das Viewer Objekt
     */
    @Override
    public Viewer viewer() {
        return info;
    }


    /**
     * Fuehrt den uebergebenen Move auf dem aktuellen Spielstand aus und
     * aktualisiert das Info Objekt.
     * Falls der Zug fehlerhaft ist, wird eine Exception geworfen.
     *
     * @param move auszufuehrender Zug
     */
    @Override
    public void make(final Move move) throws IllegalStateException {

        // falls move == null entweder fehler oder es gibt keine Moves fuer AI
        if (move == null) {
            // status aktuallisieren
            status = checkWinCond();
            if (status != Status.Ok) {
                // falls nicht Ok -> RedWin oder BlueWin, siehe checkWinCond doc
                updateInfo();
                return;
            } else {
                // fehler
                status = Status.Illegal;
                updateInfo();
                throw new IllegalStateException("make Method was called with " +
                                                "Nullpointer");
            }
        }

        // move auf richtigkeit pruefen
        if (move.getType() == MoveType.Surrender) {
            status = (currTurn == PlayerColor.Red) ?
                     Status.BlueWin : Status.RedWin;
            updateInfo();
            return;
        } else if (move.getType() == MoveType.End) {
            return;
        } else if (move.getType() != phase) {
            // Falscher Zug -> Exception werfen und status veraendern
            status = Status.Illegal;
            updateInfo();
            throw new IllegalStateException("Wrong MoveType");

        } else if (phase == MoveType.LinkLink) {
            // move ausfuehren, also informationen aktuallieseren
            if (links.contains(move.getOneLink()) &&
                    links.contains(move.getOtherLink()) &&
                    !move.getOneLink().equals(move.getOtherLink())) {
                // falls links noch vorhanden und nicht gleich, links entfernen
                links.remove(move.getOneLink());
                links.remove(move.getOtherLink());
                status = Status.Ok;
            } else {
                // Link nicht vorhanden oder gleich -> Exception werfen und
                // status veraendern
                status = Status.Illegal;
                updateInfo();
                throw new IllegalStateException("Link doesnt exist or links " +
                                                "equal each other");
            }

        } else if (phase == MoveType.AgentLink) {

            if (redPos == null || bluePos == null) {
                // falls Spieler positionen noch nicht gesetzt sind
                // (erster Zug der AgentLink phase)

                // Zielposition
                Site destSite = move.getAgent().getSecond();

                if (links.contains(move.getLink()) && !siteBlocked(destSite)) {
                    // falls Link vorhanden und Ziel nicht blockert, den Link
                    // entfernen und den Spieler bewegen.
                    links.remove(move.getLink());
                    if (currTurn == PlayerColor.Red) {
                        redPos = destSite;
                    } else {
                        bluePos = destSite;
                    }
                    // Status aktuallisieren
                    status = Status.Ok;

                } else {
                    // Sonst Fehler
                    status = Status.Illegal;
                    updateInfo();
                    throw new IllegalStateException("Site is blocked, or link" +
                                                    " doesnt exist anymore");
                }

            } else {

                // normal Fall

                Site destSite = move.getAgent().getSecond();

                // falls die Ziel Position besetzt ist (von Gegner oder man
                // selber) -> Fehler
                if (destSite.equals(redPos) || destSite.equals(bluePos)) {
                    status = Status.Illegal;
                    updateInfo();
                    throw new IllegalStateException("Site blocked");
                }

                // pruefen ob der Zug der Spielerposition moeglich ist
                if (checkAgentMove(currTurn, destSite) &&
                        links.contains(move.getLink())) {
                    // Position des Spielers anpassen und link entfernen
                    if (currTurn == PlayerColor.Red) {
                        redPos = destSite;
                    } else {
                        bluePos = destSite;
                    }
                    links.remove(move.getLink());
                    // Status aktuallisieren
                    status = Status.Ok;
                } else {
                    // Exception werfen und status veraendern
                    status = Status.Illegal;
                    updateInfo();
                    throw new IllegalStateException("Agent cant move to the " +
                                                    " desired Site, or link doesnt exist anymore");
                }
            }
        }

        // ueberpruefen ob zug zu Ende des Spiels fuehrt.
        status = checkWinCond();

        // anderer Spieler macht naechsten Zug
        currTurn = (currTurn == PlayerColor.Red) ?
                   PlayerColor.Blue : PlayerColor.Red;

        // moveCount erhoehen
        moveCount++;

        // die ersten 2^(k-1) runden werden nur links entfernt, danach mit
        // Agent moves gespielt
        if (moveCount/2 >= Math.pow(2, size_k-1)) {
            phase = MoveType.AgentLink;
        }

        // info objekt aktualiesieren
        updateInfo();

        // move in den moveBuffer speichern
        bufferMove(move);
    }



    /**
     * Macht den ersten Zug aus dem moveBuffer rueckgaengig.
     */
    public void undo() {
        if (moveBuffer[0] == null) {
            throw new IllegalStateException("Buffer is empty, no undo " +
                                            "possible");
        }
        // betreffenden Move bekommen und aus dem Buffer entfernen
        Move move = moveBuffer[0];
        moveBuffer[0] = moveBuffer[1];
        moveBuffer[1] = null;

        // falls MoveType LinkLink: links wieder hinzufuegen
        if (move.getType() == MoveType.LinkLink) {
            links.add(move.getOneLink());
            links.add(move.getOtherLink());
            // sonst zuerst Link hinzufuegen, dann betreffenden Spieler
            // zuruecksetzen
        } else if (move.getType() == MoveType.AgentLink) {
            links.add(move.getLink());
            SiteTuple tuple = move.getAgent();
            if (tuple.getSecond().equals(redPos)) {
                redPos = tuple.getFirst();
            } else {
                bluePos = tuple.getFirst();
            }
        }

        currTurn = (currTurn == PlayerColor.Red) ?
                   PlayerColor.Blue : PlayerColor.Red;

        status = Status.Ok;
        updateInfo();
    }


    /**
     * Liefert eine Collection mit allen Spielfeldern, die von dem angebenen
     * Spieler aus erreichbar sind.
     *
     * @param player Spieler fuer den die erreichbaren Felder geliefert werden
     * sollen.
     * @return Alle Felder die von dem angegebenen Spieler erreicht werden
     * koennen
     */
    public ArrayList<Site> getAccessibleSites(PlayerColor player) {

        // ermittle die Position des zu bewegenden Spielers
        Site pos = (player == PlayerColor.Red) ? redPos : bluePos;

        // Die Menge der erreichbaren Felder
        ArrayList<Site> set = new ArrayList<Site>();

        if (pos == null) {
            for (int i = 0; i < sites.length; i++) {
                for (int k = 0; k < sites[i].length; k++) {
                    if (sites[i][k] != null && !siteBlocked(sites[i][k])) {
                        set.add(sites[i][k]);
                    }
                }
            }

            return set;
        }

        // merke welcher Weg schon gepreuft wurde (mit false initialisiert)
        boolean[][] track = new boolean[size_n][];
        for (int i = 0; i < sites.length; i++) {
            track[i] = new boolean[sites[i].length];
        }


        // rufe die rekursive Funktion auf die alle erreichbaren Felder dem set
        // hinzufuegt
        getAccessibleRec(track, set, pos.getRow(), pos.getColumn());

        // die Menge der Felder zurueckgeben.
        return set;
    }


    /**
     * Ermittelt alle moeglichen zuege in der aktuellen phase des Spiels fuer
     * den angegeben Spieler.
     *
     * @param player Fuer welchen Spieler alle Zuege ermittelt werden sollen.
     * @return alle moeglichen Zuege fuer den angegebenen Spieler.
     *
     */
    public ArrayList<Move> getPossibleMoves(PlayerColor player) {
        ArrayList<Move> moves = new ArrayList<>();

        if (phase == MoveType.LinkLink) {
            // alle Kombinationen von links

            // durch links iterieren
            for (SiteSet link1 : links) {
                // durch alle links danach iterieren
                for (SiteSet link2 : links) {
                    if (link1.equals(link2)) {
                        // falls links die gleichen sind, ueberspringen
                        continue;
                    }
                    // Kombination als move der Liste anhaengen
                    moves.add(new Move(link1, link2));
                }
            }

            return moves;

        }

        if (phase == MoveType.AgentLink) {
            // alle kombinationen von accessible Sites und links
            Collection<Site> accessibleSites = getAccessibleSites(player);
            Site start = (player == PlayerColor.Red) ? redPos : bluePos;

            // durch die Sites iterieren
            for (Site dest : accessibleSites) {
                // durch die links iterieren
                for (SiteSet link : links) {
                    // kombination zu einem Move machen und der Liste anhaengen
                    moves.add(new Move(new SiteTuple(start, dest),
                                       link));
                }
            }

        }

        return moves;
    }

    //----------------------------------------------------------------
    // private mothoden

    /**
     * Aktualisert das Info Objekt mit den Informationen des Board Objekts.
     */
    @SuppressWarnings("unchecked")
    private void updateInfo() {
        // suppressing Warnigs, aufgrund des Casts der ArrayList zu Collection.
        info.redPos = redPos;
        info.bluePos = bluePos;
        info.links = (Collection<SiteSet>)links.clone();
        info.status = status;
        info.currTurn = currTurn;
        info.phase = phase;
    }


    /**
     * Speichert den uebergebenen Move im MoveBuffer an der ersten Stelle.
     * Es wird davon ausgeegangen, dass der Move nicht null ist.
     *
     * @param move zu speichernder Move
     */
    private void bufferMove(Move move) {
        if (moveBuffer[0] == null && moveBuffer[1] == null) {
            // falls buffer leer Move an 1. Stelle speichern.
            moveBuffer[0] = move;
        } else {
            // sonst ersten Move an 2. Stelle schieben und Move an 1. speichern.
            moveBuffer[1] = moveBuffer[0];
            moveBuffer[0] = move;
        }
    }


    /**
     * Ermittelt ob der angegebene Spieler eine direkte Verbindung zu der
     * angegebenen Position auf dem Spielfeld hat, um zu pruefen ob der Zug
     * gueltig ist.
     *
     * @param movedPlayer Der Spieler der auf dem Brett bewegt werden soll.
     * @param dest Zielposition des Zuges, der geprueft werden soll.
     * @return True falls der Zug moeglich ist. False, wenn nicht.
     */
    private boolean checkAgentMove(PlayerColor movedPlayer, Site dest) {
        return getAccessibleSites(movedPlayer).contains(dest);
    }



    /**
     * Prueft ob das Feld durch einen Spieler blockiert wird.
     *
     * @param site Die Site die ueberprueft werden soll.
     * @return True falls Feld blockiert.
     */
    private boolean siteBlocked(Site site) {
        return (redPos != null && redPos.equals(site)) ||
               (bluePos != null && bluePos.equals(site));
    }


    /**
     * Prueft ob ein Spieler gewonnen hat
     *
     * @return den neuen status des Spiels
     */
    private Status checkWinCond() {
        Status check = status;
        if (phase == MoveType.AgentLink) {
            // check if Players can still make a move
            PlayerColor otherPlayer = (currTurn == PlayerColor.Red) ?
                                      PlayerColor.Blue : PlayerColor.Red;
            if (getPossibleMoves(otherPlayer).isEmpty()) {
                check = (currTurn == PlayerColor.Red) ?
                        Status.RedWin : Status.BlueWin;
            }
        }

        return check;
    }




    /**
     * Fuegt alle erreichbaren Felder die noch nicht in track markiert wurden
     * zum set hinzu.
     *
     * @param track speichert welche Felder schon bearbeitet wurden
     * @param set die Menge zu der die erreichbaren Felder hinzugefuegt werden
     * sollen
     * @param row Zeile der Ausgangsposition
     * @param col Riehe der Ausgangsposition
     */
    private void getAccessibleRec(boolean[][] track, ArrayList<Site> set,
                                  int row, int col) {

        track[col][row] = true;

        // Nachbaren ermittlen
        ArrayList<Site> neighbours = getNeighbours(sites[col][row]);

        // durch Nachbaren iterieren
        for (Site testSite : neighbours) {
            //SiteSet link = new SiteSet(new Site(testSite.getRow(), testSite.getColumn()), new Site(row, col));
            SiteSet link = new SiteSet(testSite, sites[col][row]);

            // falls noch nicht markiert und erreichbar, dieses Feld dem set
            // hinzufuegen und dieselbe funktion auf dieses Feld ausfuehren

            if (!track[testSite.getColumn()][testSite.getRow()] &&
                    links.contains(link) && !siteBlocked(testSite)) {
                // erst hier wird das Feld hinzugefuegt, da sonst das ausgangs-
                // feld mit dem Spieler drauf auch in der Menge waere.
                // Koennte probleme geben (doppelte Hinzufuegung), falls
                // parallelisiert, ist es aber aktuell nicht.
                set.add(testSite);
                getAccessibleRec(track, set, testSite.getRow(),
                                 testSite.getColumn());
            }
        }
    }


    /**
     * Gibt die Menge aller benachbarten Felder der angegebenen Position auf
     * dem Brett zurueck.
     *
     * @param center Das Feld fuer das die Nachbarn ermittelt werden sollen.
     * @return Alle Nachbarn den uebergenenen Felds
     */
    private ArrayList<Site> getNeighbours(Site center) {
        // Arrays fuer Indexverschiebung in for-Schleife
        int[] colDiff = {-1, 0, 1, 1, 0, -1};
        int[] rowDiff = {0, 1, 1, 0, -1, -1};
        // Ausgangsposition
        int row = center.getRow();
        int col = center.getColumn();
        // Position der Nachbarn
        int newRow;
        int newCol;

        ArrayList<Site> neighbours = new ArrayList<Site>();

        // Indexverschiebung anwenden um auf neue angrenzendes Feld zu kommen
        for (int i = 0; i < colDiff.length; i++) {
            newRow = row + rowDiff[i];
            newCol = col + colDiff[i];

            if (newCol >= 0 && newRow >= 0 && newCol <= 10 && newRow <= 10 &&
                    siteCol.contains(new Site(newCol, newRow))) {
                // falls das Feld exisitert, das Feld der Menge der Nachbarn
                // hinzufuegen
                Site tmp = new Site(newCol, newRow);
                neighbours.add(tmp);
            }
        }
        return neighbours;
    }

}

