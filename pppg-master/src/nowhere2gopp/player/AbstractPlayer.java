package nowhere2gopp.player;

import nowhere2gopp.preset.*;
import nowhere2gopp.board.Board;
import java.rmi.RemoteException;


/**
 * Implementiert Grundfunktionalitaet, die jede (lokale) Spieler-Klasse hat.
 * Unteranderem wird implementiert, dass init, request, update, confirm
 * in der richtigen Reihenfolge aufgerufen werden. Das Ueberpruefen des
 * Status wird ebenfalls hier implementiert.
 *
 * @author bela
 */
public abstract class AbstractPlayer
    implements Player {
    /**
     * Zustaende in denen sich ein Spieler befinden kann. Diese Zustaende
     * werden benoetigt um zu ueberpruefen ob eine bestimmte Methode aufgerufen
     * werden darf (z.B. darf `confirm` nur nach `request` aufgerufen werden).
     */
    protected enum State { INIT, REQUEST, CONFIRM, UPDATE, END };

    /**
     * Speichert Zug zwischen den Methodenaufrufen `request` und `confirm`.
     */
    protected Move nextMove;
    /**
     * Speichert den derzeitigen Zustand des spielers. Wird benoetigt,
     * um zu ueberpruefen, ob die Reihenfolge der Methodenaufrufe
     * eingehalten wird (request dann confirm dann update dann ...)
     */
    protected State currState;
    /**
     * Dies ist das Board mit dem ein Spieler selber das Spiel verfolgt.
     */
    protected Board board;
    /**
     * Dies ist der view auf das spieler-interne Board. Prinzipiell koennte
     * auch immer ueber boad.viewer() auf diesen viewer zugegriffen werden,
     * aber es so zu speichern macht alles uebersichtlicher
     */
    protected Viewer view;
    /**
     * Die Farbe des Spielers.
     */
    protected PlayerColor color;

    /**
     * Initialisiert den Spieler mit einer Farbe und der Groesze des
     * Spielbretts. Es wird ein neues Board angelegt und `view` wird
     * initialisert. Auszerdem wird der Zustand (currState) auf INIT
     * gesetzt.
     *
     * @param boardSize Die Groesze des Spielbretts mit dem das neue
     * Spiel begonnen werden soll.
     * @param _color Die Farbe des Spielers (Rot oder Blau).
     */
    @Override
    public void init(int boardSize, PlayerColor _color)
    throws RemoteException {
        board = new Board(boardSize);
        view = board.viewer();
        color = _color;
        currState = State.INIT;
    }

    /**
     * Es wird ein Zug vom Spieler erfragt. Die genaue implementierung
     * ist in dieser Klasse nicht enthalten. Hier wird nur die Reihenfolge
     * der Zustaender ueberprueft und der neue Zustand gesetzt.
     * Diese Implementierung ist also nicht ausreichend und es wird von
     * Erben dieser Klasse eine nuetzliche Implementierung erwartet.
     *
     * @return Der Zug fuer den sich der Spieler "entschieden" hat.
     */
    @Override
    public Move request()
    throws RemoteException,
               MethodOutOfOrderException,
        Exception {
        // Es wird ueberprueft, ob die Reihenfolge der States eingehalten
        // wurde.
        if (currState == State.INIT && color == PlayerColor.Blue) {
            // Blau muss mit `update` anfangen.
            throw new MethodOutOfOrderException(State.REQUEST,
                                                currState, color);
        } else if (currState != State.UPDATE
                   && currState != State.INIT
                   && currState != State.REQUEST) {
            // `request` kann nur nach `update` (und `init` und `request`)
            // aufgerufen werden.
            throw new MethodOutOfOrderException(State.REQUEST, currState);
        }
        // Setze neuen Zustand
        currState = State.REQUEST;

        return null;
    }

    /**
     * Gibt eine Bestaetigung an den Spieler ob der letze Zug
     * gueltig war oder nicht oder andere Statusaenderungen mit
     * sich gebracht hat (z.B. Spielende).
     * Es wird auf ein nicht einhalten der Zustandsreihenfolge und
     * auf nicht uebereinstimmende Statusse reagiert.
     * Es ist durchaus moeglich, dass diese Implementierung alle
     * Anforderungen einer Erbenden Klasse erfuellt.
     *
     * @param status Der Status gibt an ob der letzte durch `request`
     * zurueckgegebene Zug gueltig war, zu einem Sieg gefuehrt hat etc..
     */
    @Override
    public void confirm(Status status)
    throws RemoteException,
        MethodOutOfOrderException,
        StatusOutOfSyncException,
        IllegalStateException {
        // Es wird ueberprueft ob die Reihenfolge der Zustaende eingehalten
        // wurde.
        if (currState != State.REQUEST) {
            // `confirm` kann nur nach `request` aufgerufen werden
            throw new MethodOutOfOrderException(State.CONFIRM, currState);
        }
        // Setze neuen Zustand
        currState = State.CONFIRM;
        // Wende den Zug der beim letzten Aufruf von `request`
        // zwischengespeichert wurde auf das spieler-interne Brett an
        if (nextMove != null) {
            board.make(nextMove);
            nextMove = null;
        }
        // Ueberpruefe ob eigener Status mit dem Spielstatus uebereinstimmt
        checkStatus(status);
    }

    /**
     * Diese Methode benachrichtigt den Spieler ueber den letzten Zug
     * des Gegners. Der uebergebene Zug wird auf dem spieler-internen
     * Board ausgefuehrt und der resutlierende Status mit dem uebergebenen
     * verglichen. Nicht uebereinstimmende Statusse werden mit einer
     * Exception signalisiert.
     * Diese Implementierung ist fuer die meisten erbenden Klassen vermutlich
     * ausreichend und muss nicht ueberschreiben werden.
     *
     * @param opponentMove Der Zug den der Gegenspieler als letztes gemacht hat.
     * @param status Der Status zu dem der Zug des Gegenspielers gefuehrt hat.
     */
    @Override
    public void update(Move opponentMove, Status status)
    throws RemoteException,
        MethodOutOfOrderException,
        StatusOutOfSyncException,
        IllegalStateException {
        // Es wird ueberprueft ob die Reihenfolge der Zustaende eingehalten
        // wurde.
        if (currState == State.INIT && color == PlayerColor.Red) {
            // Rot muss mit `request` anfangen.
            throw new MethodOutOfOrderException(State.UPDATE, currState, color);
        } else if (currState != State.CONFIRM && currState != State.INIT) {
            // `update` kann nur nach `confirm` (und `init`) aufgerufen werden
            throw new MethodOutOfOrderException(State.UPDATE, currState);
        }
        // Setze neuen Zustand
        currState = State.UPDATE;

        // Mache den Zug des Gegners auf dem internen Brett
        board.make(opponentMove);

        // Ueberpruefe ob eigener Status mit dem Spielstatus uebereinstimmt
        checkStatus(status);
    }

    /**
     * Eine Hilfsfunktion die ueberprueft ob der Status des Spiels
     * und der des Spielers synchron sind und andernfalls eine Exception
     * wirft.
     *
     * @param status Status des Spiels
     * @throws StatusOutOfSyncException Wird geworfen, falls der Status des
     * Spielers nicht mit dem des uebergebenen Status uebereinstimmt.
     */
    private void checkStatus(Status status)
    throws StatusOutOfSyncException {
        if (!(status == view.getStatus())) {
            // Der status des Spiels und der des Spielers sind
            // nicht synchron => Exception
            throw new StatusOutOfSyncException("AbstractPlayer.checkStatus: ",
                                               status, view.getStatus());
        }
        // Prinzipiell koennte der Spieler noch in die Zustaende `END` und
        // `ILLEGAL` versetzt werden, in denen er alle Methodenaufrufe
        // auszer `init` ablehnt, allerdings lehnt das `Board` in diesen
        // Faellen bereits Zuege ab und wirft dementsprechende Exceptions.
    }
}
