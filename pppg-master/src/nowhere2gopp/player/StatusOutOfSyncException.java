package nowhere2gopp.player;

import nowhere2gopp.preset.*;

/**
 * Exception die anzeigt, dass der Status des Spiels und der des Spielers
 * nicht synchron sind.
 *
 * @author bela
 */
public class StatusOutOfSyncException extends Exception {
    /**
     * Erzeugt Objekt das Auskunft ueber Ungleichen Status von
     * Spiel und Spieler gibt.
     *
     * @param prefix String der der Nachricht der Exception vorgestellt wird
     * um zusaetzliche Informationen zu vermitteln.
     * @param game Status des Spiels
     * @param player Status des Spielers
     */
    public StatusOutOfSyncException(String prefix, Status game, Status player) {
        super(String.format("%sStatus ist nicht synchron: "
                            +"Brett: %s, Spieler: %s",
                            prefix, game.name(), player.name()));
    }
}
