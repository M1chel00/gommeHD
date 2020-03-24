package nowhere2gopp.player;

import nowhere2gopp.preset.*;
import nowhere2gopp.board.Board;
import java.rmi.RemoteException;

/**
 * Ein Spieler der interaktiv gesteuert werden kann. Der Spieler wird mit
 * einem `Requestable`-Objekt initialisert (Konstruktor) und erfragt ueber
 * dieses Objekt Zuege.
 *
 * @author bela
 */
public class UserPlayer
    extends AbstractPlayer {

    /**
     * Wird benutzt um Zuege (interaktiv vom Benutzer) zu erfragen.
     */
    private Requestable moveSource;

    /**
     * Erzeugt ein neues Spieler-Objekt, das mithilfe von
     * `_moveSource` die Zuege vom Benutzer erfragt.
     * Vor Beginn eines Spiels muss das Spieler-Objekt noch
     * mit der `init` Methode initialisiert werden.
     *
     * @param _moveSource Das Objekt das benutzt wird um Zuege
     * interaktiv zu erfragen.
     */
    public UserPlayer(Requestable _moveSource) {
        moveSource = _moveSource;
    }

    /**
     * Es wird ein Zug vom Benutzer erfragt und an das Spiel weitergegeben.
     *
     * @return Ein interaktiv erfragter Zug. Es ist nicht garantiert, dass
     * der Move gueltig oder nicht `null` ist.
     */
    @Override
    public Move request()
    throws RemoteException,
               MethodOutOfOrderException,
        Exception {
        // Die Implementierung von `AbstracPlayer` ueberprueft, ob die
        // Reihenfolge der Zustaende etc. eingehalten wird.
        super.request();

        // Erfrage Zug vom Benutzer
        nextMove = moveSource.request();
        // Gib den Zug an das Spiel zuruck
        return nextMove;
    }
}
