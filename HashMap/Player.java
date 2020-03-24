/**
 * Die Klasse Player enthaelt die wichtigsten Informationen eines Fussballspielers.
 * 
 */
public class Player{
    /** Der Name des Fussballspielers. */
    protected String name;
    /** Der Verein des Fussballspielers. */
    protected String club;
    
    /**
     * Konstruktor.
     * @param name Der Name des Fussballspielers.
     * @param club Der Verein des Fussballspielers.
     */
    public Player(String name, String club) {
        this.name = name;
        this.club = club;
    }
    
    /**
     * Die Methode gibt den Namen des Fussballspielers zurueck.
     * @return Der Name des Fussballspielers.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Die Methode gibt den Verein des Fussballspielers zurueck.
     * @return Der Verein des Fussballspielers.
     */
    public String getClub() {
        return club;
    }
    
    /**
     * Die Methode wandelt die Daten �ber einen Fussballspieler in
     * eine Zeichenkette.
     * @return Alle Attribute dieses Objekts in lesbarer Form.
     */
    @Override
    public String toString() {
        return "Name: " + name +  "\t" + "\t" + "Verein: " + club;
    }
}   