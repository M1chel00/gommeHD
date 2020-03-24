package nowhere2gopp.player;

import nowhere2gopp.preset.*;
import nowhere2gopp.board.Board;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.Collection;


/**
 * Computer-Spieler der zufaellige aber gueltige Zuege macht.
 *
 * @author bela
 */
public class SimpleComPlayer
    extends AbstractPlayer {
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
    public SimpleComPlayer(long seed) {
        rng = new Random(seed);
    }

    /**
     * Der normale Konstruktor, der fuer ein normales Spiel verwendet werden
     * sollte.
     */
    public SimpleComPlayer() {
        rng = new Random();
    }

    /**
     * Aus der Menge aller gueltigen Zuege wird einer zufaellig ausgesucht
     * und zurueckgegeben.
     *
     * @return Ein zufaelliger aber gueltiger Zug.
     */
    public Move request()
    throws RemoteException,
               MethodOutOfOrderException,
        Exception {
        super.request();

        Collection<Move> moves = board.getPossibleMoves(this.color);
        // Da request nicht aufgerufen wird, wenn das Spiel bereits vorbei
        // ist, kann angenommen werden, dass es noch moegliche Zuege gibt.
        assert !moves.isEmpty() : "No possible moves";
        // Es wird ein Zug aus der Collection zufaellig ausgewaehlt.
        int size = moves.size();
        int rand = Math.abs(rng.nextInt()) % size;
        for (Move move : moves) {
            if (rand == 0) {
                // Gib den ausgewaehlten Zug zurueck und speicher ihn zwischen
                // um ihn in `confirm` auf das spieler-interne Brett anzuwenden.
                nextMove = move;
                break;
            }
            rand--;
        }

        // Sollte nicht erreicht werden
        assert nextMove != null : "A move should have been selected";
        return nextMove;

    }
}
