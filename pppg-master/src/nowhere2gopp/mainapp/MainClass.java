package nowhere2gopp.mainapp;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;

import nowhere2gopp.board.*;
import nowhere2gopp.inputoutput.*;
import nowhere2gopp.player.*;
import nowhere2gopp.preset.*;
import javax.swing.JOptionPane;

/**
 * Die ausfuehrbare Klasse des Projekts. Nimmt Parameter auf der Kommandozeile
 * entgegen, mit denen das Spiel gestartet wird
 * Fuehrt evtl Graphik aus
 * @author michel
 *
 */
public class MainClass {

    /**
     * Eine Referenz fuer den Type des roten Spielers.
     */
    private static PlayerType redP;

    /**
     * Eine Referenz fuer den Type des roten Spielers.
     */
    private static PlayerType blueP;

    /**
     * Eine Referenz fuer den Type des Spielers in der RMI.
     */
    private static PlayerType netPlayer;

    /**
     * Eine Referenz fuer den Delay des Spiels.
     */
    private static int delay;

    /**
     * Eine Referenz fuer die Groesze des Boards.
     */
    private static int size;

    /**
     * Eine Referenz fuer den Debug-Flag.
     */
    private static boolean isDebug = false;

    /**
     * Eine Referenz auf das Spieler Objekt des roten Spielers.
     */
    private static Player redPlayer;

    /**
     * Eine Referenz auf das Spieler Objekt blauen Spielers.
     */
    private static Player bluePlayer;

    /**
     * Eine Referenz auf die Network Flag, fuer das Spielen ueber die RMI.
     */
    private static boolean isNetwork = false;

    /**
     * Eine Referenz auf den Namen des roten Spielers, wenn dieser den Typ
     * Remote besitzt.
     */
    private static String nameRed;

    /**
     * Eine Referenz auf den Host des roten Spielers, wenn dieser den Typ
     * Remote besitzt.
     */
    private static String hostRed;

    /**
     * Eine Referenz auf den Namen des blauen Spielers, wenn dieser den Typ
     * Remote besitzt.
     */
    private static String nameBlue;

    /**
     * Eine Referenz auf den Host des blauen Spielers, wenn dieser den Typ
     * Remote besitzt.
     */
    private static String hostBlue;

    /**
     *
     * Eine Referenz auf den Namen des Spielers, wenn dieser in die RMI
     * eingetragen werden soll.
     */
    private static String netName;

    /**
     * Eine Referenz auf den Host des Spielers, wenn dieser in die RMI
     * eingetragen werden soll.
     */
    private static String netHost;

    /**
     * Eine Referenz auf das Grahik Objekt.
     */
    private static GraphicsIO graphic;

    /**
     * Eine Referenz auf die Variable fuer den GameLoop.
     * Ist diese auf 'true' und der Network Flag ist nicht gesetzt, laeuft
     * die GameLoop.
     * Ist sie auf 'false', wird das Spiel den aktuellen Zug zu Ende ausfuehren
     * und sich danach beenden.
     */
    private static boolean run = true;

    /**
     * Eine Referenz auf die Flag um externe Fenster anzuzeigen.
     * Ist dieser Wert 'false' wird nur das Graphik Fenster angezeigt, alle
     * anderen Fenster werden nicht aktiviert.
     * Diese Flag dient hauptsaechlich der statistischen Auswertung von Moves,
     * KI's, o.ae..
     */
    private static boolean showExternalWindow = true;

    public static void main(String[] args) {
        //einlesen und Verarbeitung der Argumente
        ArgumentParser argP = null;
        //try catch um Exceptions zu fangen und mit ihnen umzugehen
        try {
            //neuer ArgumentParser, der uebergebenen Argumente verarbeitet
            argP = new ArgumentParser(args);
            //setze flag werte
            showExternalWindow = !argP.getShowExternalWindow();
            isDebug = argP.isDebug();
            isNetwork = argP.isNetwork();
            //wenn networkflag gesetzt, fordere weitere Argumente ein
            if(isNetwork) {
                netName = argP.getNetName();
                netPlayer = argP.getNetPlayer();
                netHost = argP.getNetHost();
                //wird nur aufgerufen, wenn Network Flag gesetzt
                //beendet sich selbst
                startNetworkPlayer();
                return;
            }
            // Exceptionhandling fuer ArgumentParser
        } catch (ArgumentParserException e) {
            if (isNetwork) {
                //gebe Fehlermeldung mit Hinweis auf korrekte usage des Befehls
                showExceptionMessage(e, true, "{-netname <name> -netplayer " +
                                     "<Playertype> -nethost <host>}");
            }
            showExceptionMessage(e, true, "Please read the README file to " +
                                 "run the game correctly!");
        }

        try {
            //setze uebergebenen Playertyp auf Player
            blueP = argP.getBlue();
            redP = argP.getRed();
            //setze uebergebene groesze auf Size
            //pruefe auf erlaubte Groesze
            if(argP.getSize() > 11 || argP.getSize() < 3
                    || argP.getSize()%2 == 0) {
                showExceptionMessage(null, true, "Size of board is to " +
                                     "big or even!");
            }
            size = (argP.getSize()-1)/2;
        } catch(ArgumentParserException e) {
            showExceptionMessage(e, true, "Please read the README file to " +
                                 "run the game correctly!");
        }

        //sollte ein Player remote sein, muessen
        //host , port, name des Players uebergeben werden
        if (redP == PlayerType.Remote) {
            try {
                hostRed = argP.getHostRed();
                nameRed = argP.getNameRed();
            } catch (ArgumentParserException e) {
                //gebe Fehlermeldung mit Hinweis auf korrekte usage des Befehls
                showExceptionMessage(e, true, "{-hostred <host:port> " +
                                     "-namered <name>}");
            }
        }
        if (blueP == PlayerType.Remote) {
            try {
                hostBlue = argP.getHostBlue();
                nameBlue = argP.getNameBlue();
            } catch (ArgumentParserException e) {
                //gebe Fehlermeldung mit Hinweis auf korrekte usage des Befehls
                showExceptionMessage(e, true, "{-hostblue <host:port> " +
                                     "-nameblue <name>}");
            }
        }

        // wenn delay uebergeben wurde setze darauf, sonst auf 0
        try {
            delay = argP.getDelay();
            if(delay < 0) {
                showExceptionMessage(null, true, "Value of delay " +
                                     "must be positiv");
            }
        } catch (ArgumentParserException e) {
            delay = 0;
        }

        //neues Spielfeld mit uebergebener size erzeugen
        Board board = new Board(size);
        //Viewer Objekt fuer aktuelles Board
        Viewer viewer = board.viewer();
        //Grahik Objekt wird initialisiert und angezeigt
        graphic = new GraphicsIO(viewer);

        //beide Spieler werden mit uebergebenen Spielertyp initialisiert
        redPlayer = givePlayer(redP, PlayerColor.Red, viewer);
        bluePlayer = givePlayer(blueP, PlayerColor.Blue, viewer);

        //GameLoop, solange run auf true ist
        while (run) {
            //pruefe, wer dran ist
            PlayerColor currentTurn = viewer.getTurn();

            //nur zu Testzwecken
            if(isDebug)
                System.out.println(currentTurn);

            // setze delay, falls beide PlayerTypes bot
            if(delay > 0) {
                try {
                    //lege den Thread fuer den gesetzten Delay schlafen
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    showExceptionMessage(e, true);
                }
            }

            //Move Objekt fuer aktuellen Move
            Move currentMove;

            //setzt currentMove auf eingebenen Move des aktuellen Spielers
            currentMove = (currentTurn == PlayerColor.Red) ?
                          (playerMove(redPlayer, viewer, bluePlayer, board)) :
                          (playerMove(bluePlayer, viewer, redPlayer, board));

            // aktuelle Stand des Spiels wird ausgegeben
            // pruefe Status Spieler, gibt evtl Win/Lose aus
            Status status = viewer.getStatus();

            //nur zu Testzwecken
            if(isDebug && status != null)
                System.out.println(status.toString());

            //wenn Status auf Win ist, wird Spiel abgebrochen und Message
            //ausgegeben
            if(status == Status.RedWin || status ==  Status.BlueWin) {
                showExceptionMessage(null, true, status.toString());
            }
        }
    }

    /**
     * Gibt ein Spielerobjekt des ueber die Kommandozeile angegeben Spielertyp
     * zurueck
     * @param player Gibt den Typ des Spielers an
     * @param color Gibt die Farbe des Spielers an
     * @param viewer Ein viewer Objekt des Spielbrettes
     * @return Spielerobjekt des uebergebenen Spielertyps
     */
    private static Player givePlayer(PlayerType player,
                                     PlayerColor color, Viewer viewer) {
        //Player Objekt zum zurueckgeben
        Player returnPlayer = null;
        //checkt Spielertypen, initialisiert Spieler und gibt sie zurueck
        switch (player) {
        case Human:
            returnPlayer = new UserPlayer(graphic);
            break;
        case RandomAI:
            returnPlayer = new SimpleComPlayer();
            break;
        case SimpleAI:
            returnPlayer = new ExtendedComPlayer();
            break;
        case Remote:
            //die jeweiligen Spieler aus dem Netzwork holen
            if (color == PlayerColor.Red) {
                returnPlayer = (Player) find(hostRed, nameRed);
            } else {
                returnPlayer = (Player) find(hostBlue, nameBlue);
            }
            break;
        //sollte falscher Typ uebergeben werden, beende Programm und
        //gib Message aus
        default:
            showExceptionMessage(null, true, "not a valid " +
                                 "Playertype\n valid: {human, simple, random, remote}");
        }
        //Spieler mit groesze des Spielfeldes und der Farbe initialisieren
        try {
            returnPlayer.init(size, color);
        } catch (RemoteException e) {
            showExceptionMessage(e, true);
        } catch (Exception e) {
            showExceptionMessage(e, true);
        }
        return returnPlayer;
    }

    /**
     * Fragt uebergebenen Spieler nach dem Zug, bestaetigt diesen, fuehrt in
     * auf dem Spielfeld aus, updatet ihn beim Gegner und setzt den Status
     * in der Graphik
     * @param player Der Spieler, von dem der Zug abgefragt werden soll
     * @param viewer Viewer Objekt des Spielbrettes //evtl unnoetig
     * @param opponent Gegenspieler des angefragten Spielers
     * @param board Board Objekt des aktuellen Boards
     * @return Move Objekt des angefragten Zuges
     */
    private static Move playerMove(Player player,
                                   Viewer viewer, Player opponent, Board board) {
        //Move objekt fuer angefragten Move
        Move currentMove = null;
        try {
            //fragt Zug an
            currentMove = player.request();
            //fuehrt zug auf dem Board durch
            board.make(currentMove);
            //bestaetigt Zug
            player.confirm(viewer.getStatus());
            //update Zug beim Gegner
            opponent.update(currentMove, viewer.getStatus());
            //update Graphik
            graphic.repaint();
        } catch (MethodOutOfOrderException e) {
            showExceptionMessage(e, true);
        } catch (RemoteException e) {
            showExceptionMessage(e, true);
        } catch (IllegalStateException e) {
            showExceptionMessage(e, false, "Error while entering a move!\n" +
                                 "Please enter a valid move!");
        } catch (StatusOutOfSyncException e) {
            showExceptionMessage(e, true);
        } catch (Exception e) {
            showExceptionMessage(e, true);
        }
        //gibt angefragten Move zurueck
        return currentMove;
    }

    /**
    * Interne Funktion fuer das Handling von Exception
    * Wenn die Debug Flag gesetzt ist, wird der Exception StackTrace
    * ausgegeben und das Programm beendet sich, wenn der boolean 'shutdown'
    * auf 'true' gesetzt ist.
    * @param e Exception, die zu behandelnde Exception
    * @param shutdown boolean, ob das Programm beendet werden soll
    */
    public static void showExceptionMessage(Exception e, boolean shutdown) {
        //wenn debug flag gesetzt und exception nicht null, gibt stackTrace aus
        if(isDebug && e != null) {
            e.printStackTrace();
        }
        //wenn externalWindow Flag gesetzt, zeige externes Fenster mit
        //Fehlermeldung an
        if(showExternalWindow) {
            JOptionPane.showMessageDialog(null, "An internal error occured!");
        }
        //wenn shutdown flag gesetz, beende Programm
        if(shutdown) {
            System.exit(0);
        }
    }

    /**
    * Interne Funktion fuer das Handling von Exception
    * Wenn die Debug Flag gesetzt ist, wird der Exception StackTrace
    * ausgegeben und das Programm beendet sich, wenn der boolean 'shutdown'
    * auf 'true' gesetzt ist.
    * Die uebergebene Message wird in einem externen Fenster angezeigt.
    * @param e Exception, die zu behandelnde Exception
    * @param shutdown boolean, ob das Programm beendet werden soll
    * @param message String, die auszugebende Message
    */
    public static void showExceptionMessage(Exception e, boolean shutdown,
                                            String message) {
        //wenn debug flag gesetzt und exception nicht null, gibt stackTrace aus
        if(isDebug && e != null) {
            e.printStackTrace();
        }
        //wenn externalWindow Flag gesetzt, zeige externes Fenster mit
        //Fehlermeldung an (mit uebergebener message)
        if(showExternalWindow) {
            JOptionPane.showMessageDialog(null, message);
        }
        //wenn shutdown flag gesetzt, beende Programm
        if(shutdown) {
            System.exit(0);
        }
    }

    /**
    * Sucht einen Spieler in der Registry und gibt diesen zurueck
    * @param host String fuer host des zu suchenden Players
    * @param name String fuer name des zu suchenden Players
    * @return Player den gefundenen Player
    */
    private static Player find(String host, String name) {
        Player p = null;
        try {
            //sucht Spieler in der registry und weist ihn p zu
            p = (Player) Naming.lookup("rmi://" + host + "/" + name);
        } catch(MalformedURLException | RemoteException | NotBoundException e) {
            showExceptionMessage(e, true, "Could not find the Networkplayer!");
        }
        //wenn debug flag gesetzt, gib aus, dass der Spieler gefunden wurde
        if(isDebug) {
            System.out.println("Player (" + name + ") found");
        }
        //gibt den gefundenen Spieler zurueck
        return p;
    }

    /**
    * Stellt einen Spieler in die Registry
    * @param player Player der in die Registry zu stellende Player
    * @param host String fuer host des in die Registry zustellenden Players
    * @param name String fuer name des in die Registry zu stellenden Players
    */
    private static void offer(Player player, String host, String name) {
        try {
            //stellt uebergebenen Spieler in uebergebene registry
            Naming.rebind("rmi://" + host + "/" + name, player);
            //gibt aus, wenn Spieler bereit ist
            System.out.println("Player (" + name + ") ready");
            //beendet Programm bei Exceptions
        } catch(MalformedURLException e) {
            showExceptionMessage(e, true);
        } catch(RemoteException e) {
            showExceptionMessage(e, true);
        }
    }

    /**
    * 'GameLoop' falls man selbst NetworkPlayer ist
    */
    private static void startNetworkPlayer() {
        Player p = null;
        try {
            //geht auf der Kommandozeile uebergebene PlayerTypen durch
            //erstellt neuen Spieler des Typen
            switch (netPlayer) {
            case Human:
                p = new NetworkPlayer(PlayerType.Human);
                break;
            case RandomAI:
                p = new NetworkPlayer(PlayerType.RandomAI);
                break;
            case SimpleAI:
                p = new NetworkPlayer(PlayerType.SimpleAI);
                break;
            default:
                showExceptionMessage(null, true, "not a valid " +
                                     "Playertype\n valid: {human, simple, random}");
                break;
            }
            //bei Exception beende Programm
        } catch (RemoteException e) {
            showExceptionMessage(e, true);
        }
        //stelle Spieler in die registry
        offer(p, netHost, netName);
    }
}
