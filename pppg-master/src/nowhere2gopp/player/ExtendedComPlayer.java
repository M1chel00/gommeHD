package nowhere2gopp.player;

import nowhere2gopp.preset.*;
import nowhere2gopp.board.Board;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Computer-Spieler der nach einer einfachen Taktik vorgeht um zu gewinnen.
 *
 * @author bela
 */
public class ExtendedComPlayer
    extends AbstractPlayer {

    /**
     * Wird als Random Number Generator benutzt. Kann mit einem Konstruktor
     * mit einem festen Seed initialisiert werden, damit debugging leichter
     * moeglich ist.
     */
    private Random rng;

    /**
     * Konstruktor zum erleichtern von Debugging. Der uebergebene Seed wird
     * benutzt um einen Random-Number-Generator zu initialisiren. Bei gelichem
     * Seed werden von diesem Spieler auch die Selben Zuege gemacht
     * (jedenfalls wenn der Verlauf des gesamten Spiels ebenfalls der
     * Selbe ist).
     *
     * @param seed Der Seed der benutzt werden soll um den RNG zu
     * initialisieren.
     */
    public ExtendedComPlayer(long seed) {
        rng = new Random(seed);
    }

    /**
     * Der normale Konstruktor, der fuer ein normales Spiel verwendet werden
     * sollte.
     */
    public ExtendedComPlayer() {
        rng = new Random();
    }

    public Move request()
    throws Exception,
        RemoteException {
        super.request();
        if (view.getPhase() == MoveType.LinkLink) {
            nextMove = getRandomMove();
        } else if (view.getPhase() == MoveType.AgentLink) {
            nextMove = getBestMove();
        }

        // Die Methode sollte in jedem Fall einen Zug bestimmen koennen.
        // Sie kann nur versagen, wenn es keine moeglichen Zuege mehr gibt.
        assert nextMove != null :
        "ExtendenComPlayer.request(): Es konnte kein Zug bestimmt werden.";

        return nextMove;
    }

    /**
     * Berechnet das Gewicht der Zusammenghangskomponente (Connection
     * Component Weight) der Sites die fuer einen Spieler erreichbar
     * sind und der Links die noch vorhanden sind.
     *
     * @param color Farbe des Spielers fuer den Gewicht berechnet werden soll.
     * @param board Spiel(-brett) fuer dass das Gewicht berechnet werden soll.
     * @return Gewicht der Zusammgenhangskomponente.
     */
    public static int getConCompWeight(PlayerColor color, Board board) {
        // Erfrage die erreichbaren Sites.
        Collection<Site> sites = board.getAccessibleSites(color);

        // Es muss nurnoch die Menge der Links berechnet werden. Alle
        // Sites die vom Spieler erreichbar sind, muessen auch untereinander
        // verbunden sein.
        //
        // Um festzustellen ob ein Link in der Zusammenhangskomponente enthalten
        // ist, muss nur festgestellt werden, ob die Sites die vom Link
        // verbunden werden beide Teil der ZK sind.
        int linkCount = 0;
        for (SiteSet set : board.viewer().getLinks()) {
            if (sites.contains(set.getFirst())
                    && sites.contains(set.getSecond())) {
                linkCount++;
            }
        }

        return sites.size() + linkCount;
    }

    /**
     * Gibt einen zufaelligen, gueltigen Spielzug zurueck, oder `null` falls
     * kein Spielzug moeglich ist.
     *
     * @return Der ausgewaehlte Spielzug oder `null`
     */
    private Move getRandomMove() {
        // Generiere zufaelligen, gueltigen Spielzug.
        Collection<Move> moves = board.getPossibleMoves(this.color);

        // Es muss moegliche Zuege geben
        assert !moves.isEmpty() :
        "getRandomMove() wurde aufgerufen obwohl keine Zuege moeglich sind.";

        // Es wird ein Zug aus der Collection zufaellig ausgewaehlt.
        int size = moves.size();
        int rand = Math.abs(rng.nextInt()) % size;
        for (Move move : moves) {
            if (rand == 0) {
                // Gib den ausgewaehlten Zug zurueck
                return move;
            }
            rand--;
        }

        // Nicht erreichbar
        assert true : "Sollte nicht erreichbar sein";
        return null;
    }

    /**
     * Berechnet Zuege im vorraus, um einen moeglichst guten Zug von allen
     * moeglichen auszuwaehlen.
     *
     * @return Der ausgewaehlte Zug oder `null`
     */
    private Move getBestMove() {

        // Das ist nur fuer die Lesbarkeit
        PlayerColor ownColor = this.color;
        PlayerColor oppColor = (this.color == PlayerColor.Blue) ? (PlayerColor.Red)
                               :(PlayerColor.Blue);
        // MIN_VALUE, damit auf jeden Fall im ersten Durchgang ein groesserer Wert
        // gefunden wird.
        int maxMinRating = Integer.MIN_VALUE;
        List<Move> bestMoves = new ArrayList<Move>();

        outerLoop:
	for (Move ownMove : board.getPossibleMoves(ownColor)) {
            // MAX_VALUE, damit ebenfalls im ersten Durchgang ein kleinerer Wert
            // gefunden wird.
            int minRating = Integer.MAX_VALUE;
            // Probiere den Zug aus.
            board.make(ownMove);

            switch (view.getStatus()) {
            // Ueberpruefe was der Zug fuer auswirkungen hat.
            case RedWin:
                if (ownColor == PlayerColor.Red) {

                    // Spiel wird mit dem Zug gewonnen -> Es gibt keinen besseren Zug
                    // Mach den eigenen Zug der ausgetestet wurde rueckgaengig
                    board.undo();
                    return ownMove;
                } else {
                    // Spiel wird mit dem Zug verloren -> Es gibt keinen schlechteren Zug
                    minRating = Integer.MIN_VALUE;
                }
                break;
            case BlueWin:
                if (ownColor == PlayerColor.Blue) {
                    // Spiel wird mit dem Zug gewonnen -> Es gibt keinen besseren Zug
                    // Mach den eigenen Zug der ausgetestet wurde rueckgaengig
                    board.undo();
                    return ownMove;
                } else {
                    // Spiel wird mit dem Zug verloren -> Es gibt keinen schlechteren Zug
                    minRating = Integer.MIN_VALUE;
                }
                break;
            case Ok:
                // Probiere jeden Zug des Gegners aus der als Reaktion moeglich ist
                // und betrachte die Situation danach.
                for (Move oppMove : board.getPossibleMoves(oppColor)) {
                    // Initialisierungswert nur fuer den Compiler wichtig
                    int rating = 0;

                    // Mach den Gegner-Zug und guck wie gut/schlecht er ist.
                    board.make(oppMove);

                    // Ueberpruefe ob der Gegener durch den Zug gewinnt.
		    Status stat = view.getStatus();
		    boolean oppWin = (stat == Status.RedWin) && ownColor != PlayerColor.Red
			|| (stat == Status.BlueWin) && ownColor != PlayerColor.Blue;

                    // Bewerte den Gegner-Zug.
                    if (oppWin) {
                        // Zug fuehrt zum Sieg fuer den Gegner
			// => schlechtest moegliche Bewertung
                        rating = Integer.MIN_VALUE;
                    } else {
                        // Berechne das normale Rating
                        rating = getConCompWeight(ownColor, board)
                                 - getConCompWeight(oppColor, board);
                    }

                    // Update `minRating` mit dem neuen Minimum
                    minRating = Math.min(minRating, rating);

                    // Mach den Gegner-Zug wieder rueckgaengig.
                    board.undo();

		    if (minRating < maxMinRating) {
                        // Es wurde schon ein eigener Zug gefunden, der besser ist
			// => naechster eigener Zug

			// Mache den eigenen Zug rueckgaengig
                        board.undo();
			// Und gehe in die Schleife, um den naechsten eigenen
			// Zug auszuprobieren.
			continue outerLoop;
		    }
                }
                break;
            }

            // Guck ob der eigene Zug besser war als alle anderen bisher, oder
            // so gut wie die besten bisher, oder schlechter.
            if (minRating > maxMinRating) {
                // Der Zug war der beste bis jetzt
                // => verwerfe alle bisherigen und speichere nur diesen Zug
                maxMinRating = minRating;
		bestMoves.clear();
                bestMoves.add(ownMove);
            } else if (minRating == maxMinRating) {
                // Der Zug war einer der Besten
                // => speichere ihn
                bestMoves.add(ownMove);
            }

            // Mach den eigenen Zug der ausgetestet wurde rueckgaengig
            board.undo();
        }

        // `bestMoves` kann nur leer sein, wenn von vornherein keine Zuege moeglich
        // waren.
        assert !bestMoves.isEmpty() :
        "bestMoves kann nicht leer sein.";

        // Waehle von den besten Zuegen zufaellig einen aus.
        int rand = Math.abs(rng.nextInt()) % bestMoves.size();
        Move move = bestMoves.get(rand);
        return move;
    }
}
