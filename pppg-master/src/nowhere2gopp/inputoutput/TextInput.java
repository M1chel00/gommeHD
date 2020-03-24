package nowhere2gopp.inputoutput;
import nowhere2gopp.preset.*;

import java.util.Collection;
import java.util.Scanner;

/**
 * Klasse zum Verarbeiten von Texteingaben
 * @author Jan
 */
class TextInput implements Requestable {
    private Viewer boardView;

    public TextInput(Viewer _boardView) {
        boardView = _boardView;
    }

    /**
     * Die Methode gibt eine Aufforderung zur eingabe des Zugs aus.
     * Anschliessend wird ein String eingelsen
     * @return Move Objekt
     */
    @Override
    public Move request() {
        //ueber den Scanner wird ein String eigelsen
        //System.in ist der Input Stream
        Scanner in = new Scanner(System.in);
        Move returnValue = null;

        if(boardView != null) {
            printLinks();
            printAgents();
            printPhases();
        }

        boolean correctInput;
        do {
            System.out.println("Enter your move:");
            try {
                returnValue = Move.parse(in.nextLine());
                correctInput = true;
            } catch (MoveFormatException | SitePairFormatException e) {
                System.out.println(e.getMessage());
                correctInput = false;
            }
        } while (!correctInput);
        return returnValue;
    }

    /**
     * Gibt die Position der Agents aus
     */
    private void printAgents() {
        Site blueSite = boardView.getAgent(PlayerColor.Blue);
        Site redSite = boardView.getAgent(PlayerColor.Red);

        if(blueSite != null) {
            System.out.println("Position Blue Player: " + blueSite);
        }
        if(redSite != null) {
            System.out.println("Position Red Player: " + redSite);
        }
    }

    /**
     * Gibt alle aktuellen Links aus
     */
    private void printLinks() {
        Collection<SiteSet> links = boardView.getLinks();
        System.out.println("Curent Links:");
        for(SiteSet s : links) {
            System.out.println(s);
        }
    }

    /**
     * Gibt die Aktuelle Phase des Spiels aus
     */
    private void printPhases() {
        System.out.println("Current phase: " + boardView.getPhase());
    }
}
