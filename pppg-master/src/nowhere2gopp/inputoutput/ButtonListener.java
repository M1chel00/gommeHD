package nowhere2gopp.inputoutput;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Klasse zum verwalten des Klick events
 * @author Jan
 */
class ButtonListener implements ActionListener {
    /**
     * Movemanager um das Event weiter zu reichen
     */
    private MoveManager moveManager;

    /**
     * Konstruktor der Klasse
     * @param _moveManager Move Manager zur verarbeitung des Klicks
     */
    ButtonListener(MoveManager _moveManager) {
        moveManager = _moveManager;
    }


    /**
     * Wird beim Klicken des Buttons aufgeführt
     * @param e Event Parameter
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        moveManager.surrenderButtonClicked();
    }
}
