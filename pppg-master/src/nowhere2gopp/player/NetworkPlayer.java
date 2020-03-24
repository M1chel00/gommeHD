package nowhere2gopp.player;

import nowhere2gopp.preset.*;
import nowhere2gopp.board.Board;
import nowhere2gopp.mainapp.MainClass;
import nowhere2gopp.inputoutput.GraphicsIO;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Eine Wrapper Klasse um `Player`, die Spiele ueber das Netzwerk
 * ermoeglicht. Es wird die Schnittstelle `Viewable` implementiert,
 * damit das Spiel auch auf einem Computer verfolgt werden kann,
 * auf dem nur der `NetworkPlayer` nicht aber das tatsaechliche
 * Spiel laeuft.
 *
 * @author bela
 * @author michel
 */
public class NetworkPlayer
    extends UnicastRemoteObject
    implements Viewable, Player {

    /**
     * Einer Referenz auf eine tatsaechliche Implementierung eines
     * Spielers.
     */
    private Player localPlayer;

    /**
     * Wird benutzt um einen Zug zwischen den Aufrufen von `request` und
     * `confirm` zwischen zu speichern.
     */
    private Move lastMove;

    /**
     * Damit das Spiel auch auf einem Computer verfolgt werden kann,
     * auf dem nur der Netzwerkspieler laeuft, muss `NetworkPlayer`
     * ein `Board` haben um die Spielsituation verfolgen zu koennen
     * und das Interface `Viewable` zu implementieren, da `Player`
     * keine Moeglichkeit vorsieht das spieler-interne `Board` von
     * auszen erreichbar zu machen.
     */
    private Board board;

    /**
    * Referenz auf PlayerType des uebergebenen Spielers
    */
    private PlayerType playerType;

    /**
    * Referenz auf GraphicsIO
    */
    private GraphicsIO graphic;

    /**
     * Erstellt einen NetzwerkSpieler der als Wrapper um den uebergebenen
     * Spieler fungiert.
     *
     * @param type Der Spielertype um den ein Wrapper erzeugt wird.
     * (Darf nicht `null` sein.)
     * @throws RemoteException Weil er von UnicastRemoteObject erbt
     */
    public NetworkPlayer(PlayerType type)
    throws RemoteException {
        assert type != null :
        "NetworkPlayer(_localPlayer): _localPlayer kann nicht `null` sein";
        playerType = type;
    }

    /**
     * Gibt den `Viewer` auf das interne Spielbrett zurueck, damit das
     * Spiel auch auf einem Rechner verfolgt werden kann, auf dem nur
     * der Netzwerkspieler laeuft.
     */
    public Viewer viewer() {
        return board.viewer();
    }

    /**
     * Es wird ein Zug vom Benutzer erfragt und an das Spiel weitergegeben.
     */
    public Move request()
    throws Exception, RemoteException {
        // Speichere den Zug zwischen um ihn spaeter beim Aufruf von
        // `confirm` auf das interne Brett anwenden zu koennen.
        lastMove = localPlayer.request();
        return lastMove;
    }

    /**
     * Gibt eine Bestaetigung an den Spieler ob der letze Zug
     * gueltig war oder nicht oder andere Statusaenderungen mit
     * sich gebracht hat (z.B. Spielende).
     * Die Graphik wird neugezeichnet und evtl eine Win-message ausgegeben
     *
     * @param status Der Status gibt an ob der letzte durch `request`
     * zurueckgegebene Zug gueltig war, zu einem Sieg gefuehrt hat etc..
     */
    public void confirm(Status status)
    throws Exception, RemoteException {
        // Gib den Status an den Spieler weiter und wende den vorher
        // gespeicherten Zug auf das Brett an.
        localPlayer.confirm(status);
        if (lastMove != null) {
            board.make(lastMove);
            lastMove = null;
        }
        //malt das Bild neu
        graphic.repaint();
        //gibt Status aus und beendet nicht, das Programm
        if(status == Status.RedWin || status == Status.BlueWin) {
            MainClass.showExceptionMessage(null, false, status.toString());
        }
    }

    /**
     * Diese Methode benachrichtigt den Spieler ueber den letzten Zug
     * des Gegners. Der uebergebene Zug wird auf dem spieler-internen
     * Board ausgefuehrt.
     * Die Graphik wird neugezeichnet und evtl eine Win-message ausgegeben
     *
     * @param opponentMove Der Zug den der Gegenspieler als letztes gemacht hat.
     * @param status Der Status zu dem der Zug des Gegenspielers gefuehrt hat.
     */
    public void update(Move opponentMove, Status status)
    throws Exception, RemoteException {
        //rufe update() vom localPlayer auf
        localPlayer.update(opponentMove, status);
        // Der Spieler sollte ueberpruefen ob der status richtig ist.
        // Also muss der Netzwerkspieler ihn nicht ueberpruefen.
        if (opponentMove != null) {
            board.make(opponentMove);
        }
        //malt das Bild neu
        graphic.repaint();
        //gibt Status aus und beendet nicht das Programm
        if(status == Status.RedWin || status == Status.BlueWin) {
            MainClass.showExceptionMessage(null, false, status.toString());
        }
    }

    /**
     * Initialisiert den Spieler mit einer Farbe und der Groesze des
     * Spielbretts. Es wird ein neues Board angelegt und die Graphik.
     *
     *
     * @param boardSize Die Groesze des Spielbretts mit dem das neue
     * Spiel begonnen werden soll.
     * @param color Die Farbe des Spielers (Rot oder Blau).
     */
    public void init(int boardSize, PlayerColor color)
    throws Exception, RemoteException {
        //erstellt neues Board mit uebergebener size
        board = new Board(boardSize);
        //erstellt neue Graphik
        graphic = new GraphicsIO(board.viewer());
        //unterscheidet playerTypen
        switch(playerType) {
        case Human:
            localPlayer = new UserPlayer(graphic);
            break;
        case RandomAI:
            localPlayer = new SimpleComPlayer();
            break;
        case SimpleAI:
            localPlayer = new ExtendedComPlayer();
            break;
        default:
            MainClass.showExceptionMessage(null, true, "not a valid " +
                                           "Playertype\n valid: {human, simple, random}");
        }
        //initialisiert Spieler mit uebergebenen Parametern
        localPlayer.init(boardSize, color);
    }
}
