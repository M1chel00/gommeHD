package nowhere2gopp.inputoutput;

import nowhere2gopp.preset.*;

import javax.swing.*;
import java.awt.*;

/**
 * Klasse fuer die Gesamte Grafische ausgabe
 * @author Jan
 */
public class GraphicsIO implements Requestable {
    /**
     * Zu zeigender Frame
     */
    private JFrame frame;
    /**
     * Objekt zum abrufen des Zustands des Boards
     */
    private Viewer boardView;
    /**
     * Panel zum Zeichnen des Spielfelds
     */
    private GraphicsBoard graphicsBoard;
    /**
     * Label zum zeigen des Aktuellen Status
     */
    private JLabel moveTypeIndicator;
    /**
     * Label zum zeigen welcher Spieler nun an der Reihe ist
     */
    private JLabel currentPlayerIndicator;
    /**
     * Button zum Aufgeben
     */
    private JButton surrenderButton;
    /**
     * Label zum zeigen des Aktuellen Status
     */
    private JLabel statusIndicator;


    /**
     * Konstruktor
     * @param _boardView Viewer Objekt des Boards
     */
    public GraphicsIO(Viewer _boardView) {

        boardView = _boardView;
        frame = new JFrame("pppg");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel southPanel = new JPanel();
        southPanel.setBackground(Color.GRAY);
        southPanel.setLayout((new BoxLayout(southPanel, BoxLayout.PAGE_AXIS)));
        currentPlayerIndicator = new JLabel("Current Player: ");
        southPanel.add(currentPlayerIndicator);
        statusIndicator = new JLabel("Status: ");
        southPanel.add(statusIndicator);
        moveTypeIndicator = new JLabel("Currently not your turn");
        southPanel.add(moveTypeIndicator);

        graphicsBoard = new GraphicsBoard(boardView, this);

        surrenderButton = new JButton("Surrender");

        ButtonListener buttonListener
            = new ButtonListener(graphicsBoard.getMoveManager());
        surrenderButton.addActionListener(buttonListener);

        southPanel.add(surrenderButton);

        southPanel.add(Box.createHorizontalGlue());

        setEnableButtons(false);

        frame.add(graphicsBoard);
        frame.add(southPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);

    }

    /**
     * Aktiviert bzw. Deaktiviert alle Buttons
     * @param b neuer Zustand der Buttons
     */
    void setEnableButtons(boolean b) {
        surrenderButton.setEnabled(b);
    }

    /**
     * Ändert den angezeigten Status
     * @param s neuer Status
     */
    public void setMove(String s) {
        moveTypeIndicator.setText(s);
    }

    /**
     * Fügt etwas zum Aktuellen Status hinzu
     * @param s Neuer Teil des Status
     */
    void addToMove(String s) {
        String oldStatus = moveTypeIndicator.getText();
        String newStatus = oldStatus + " " + s;
        moveTypeIndicator.setText(newStatus);
    }

    private void setCurrentPlayer(PlayerColor color) {
        String s = "Current Player: ";
        if (color == PlayerColor.Blue) {
            s += "Blue";
        } else {
            s += "Red";
        }
        currentPlayerIndicator.setText(s);
    }

    private void setStatus(Status status) {
        String s = "Status: " + status;
        statusIndicator.setText(s);
    }

    /**
     * Fragt einen Zug an
     * @return Gibt zug zurueck
     */
    @Override
    public Move request() {
        frame.repaint();
        MoveManager moveManager = graphicsBoard.getMoveManager();
        Object monitor = moveManager.getMonitor();

        if(boardView.getStatus() == Status.BlueWin) {
            setMove("Blue Won");
            return null;
        } else if (boardView.getStatus() == Status.RedWin) {
            setMove("Red Won");
            return null;
        }

        setCurrentPlayer(boardView.getTurn());
        setStatus(boardView.getStatus());

        if(boardView.getPhase() == MoveType.LinkLink) {
            setMove("Make a Link Link move");
            moveManager.linkLinkMoveRequested();
        } else if (boardView.getPhase() == MoveType.AgentLink) {
            setMove("Make a Agent Link move");
            moveManager.agentLinkMoveRequested();
            Site currentSite = boardView.getAgent(boardView.getTurn());
            moveManager.addSite(currentSite);
        }

        synchronized(monitor) {
            try {
                monitor.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        setEnableButtons(false);
        return moveManager.getMove();

    }

    public void repaint() {
        frame.repaint();

        setCurrentPlayer(boardView.getTurn());

        setMove(boardView.getPhase().toString());

        setStatus(boardView.getStatus());
    }
}
