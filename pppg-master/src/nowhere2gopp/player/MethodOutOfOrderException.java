package nowhere2gopp.player;

import nowhere2gopp.preset.*;
import nowhere2gopp.player.AbstractPlayer.State;

/**
 * Exception die geworfen wird, wenn die vorgegebene Reihenfolge in der
 * Methoden aufgerufen werden sollen gebrochen wird.
 * Die Reihenfolge wird als folge von Zustaenden (States) gesehen.
 * Der Aufruf einer Methode fuehrt von einem Zustand in den naechsten.
 *
 * @author bela
 */
public class MethodOutOfOrderException extends IllegalStateException {
    /**
     * Resultierende Exception enthaelt Informationen ueber den urspruenglichen
     * Zustand und den Zustand in den gewechselt werden sollte.
     *
     * @param call Zustand in den gewechselt werden sollte.
     * @param curr Vorheriger Zustand.
     */
    public MethodOutOfOrderException(State call, State curr) {
        super(String.format("Wechsel von Zustand `%s` nach `%s`",
                            curr.name(), call.name()));
    }

    /**
     * Resultierende Exception enthaelt zusaetzlich zu den Informationen
     * ueber Zustaende auch noch Informationen ueber die Farbe des Spielers.
     *
     * @param call Zustand in den gewechselt werden sollte.
     * @param curr Vorheriger Zustand
     * @param color Farbe des Spielers der den Zustand wechseln sollte.
     */
    public MethodOutOfOrderException(State call,
                                     State curr,
                                     PlayerColor color) {
        super(String.format("Wechsel von Zustand `%s` nach `%s`"
                            +" beim Spieler `%s`",
                            curr.name(), call.name(), color.name()));
    }
}
