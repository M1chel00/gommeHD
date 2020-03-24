/**
 * HashMap Klasse, die mit Generics angelegt ist, wodurch sie unabhaengig von dieser Aufgabe (mit Player) ebenso 
 * funktionieren kann.
 * Legt Array der doppelten Groesze der uebergebenen Elemente an und bietet Funktionen put, get und getLength an
 * @author michel
 *
 * @param <T> Objekt, mit welchem das Array angelegt wird
 */
public class HashMap<T>{

	private T[] map;
	
	/**
	 * Konstruktor des HashMap Objekts, welches einen Integer als Parameter fuer
	 * die Anzahl an Elemente, die in dieser gespeichert werden sollen.
	 * Ein Array wird dann mit doppelter Groesze angelegt, damit das Suchen vernuenftig
	 * funktionieren kann.
	 * @param size Integer fuer Anzahl an Elemente in der HashMap
	 */
	public HashMap(int size) {		//size = n, Anzahl an Elemente
		map = (T[])new Object[2*size];		//doppelt so viele Buckets werden angelegt
	}
	
	/**
	 * Funktion zum Hinzufuegen eines Objektes/Elementes zur HashMap/Array
	 * @param obj	Das hinzuzufuegende Objekt
	 * @param index	Integer, Stelle an der es hinzugefuegt werden soll
	 * @return	Gibt bei erfolgreichem hinzufuegen true, sonst false zurueck
	 */
	public boolean put(T obj, int index) {
		if(obj == null)		//wenn das Objekt null ist, wird direkt false zurueckgegeben
			return false;
		
		while(map[index] != null) {		//wenn die Stelle, an der das Objekt hinzugefuegt werden
										//soll bereits belegt ist, wird der index durch die Ausweichsfunktion
			 							//neu berechnet und diese Stelle wird erneut geprueft
										//solange, bis eine leere Stelle im Array gefunden wurde
			//Ausweichfunktion			
			index = (index + 1) % (map.length);
		}		
										//objekt wird an die Stelle gesetzt
		map[index] = obj;
		return true;					//erfolgreich -> gibt true zurueck
	}
	
	/**
	 * Gibt das Objekt zurueck, welches an der Stelle index im Array steht
	 * @param index	Integer die herauszusuchende Stelle
	 * @return	Gibt das Objekt an der Stelle index zurueck
	 */
	public T get(int index) {
		return map[index];
	}
	
	/**
	 * Liefert ein Array mit allen Objekten der HashMap vom Index bis zum nächsten null Element
	 * @param index	Integer ab dem gesucht werden soll
	 * @return	Liefert ein Array mit allen Objekten der HashMap vom Index bis zum nächsten null Element
	 */
	public Object[] getArray(int index) {
		int cursor = index;		//setzt cursor auf uebergebenen index
		T[] array = (T[])new Object[0];		//legt 2 neue Arrays der Groesze 0 an
		T[] array2 = (T[])new Object[0];
		while(map[cursor] != null) {		//geht die HashMap bis zum naechsten null Element durch
			array2 = (T[])new Object[array.length+1];	//vergroeszert das Array um 1
			for(int i = 0; i < array.length; i++) {		//weist alle Element vom array dem array2 zu
				array2[i] = array[i];
			}
			array2[array.length] = map[cursor];		//fuegt an die letzte Stelle das Element der Hashmap an der Stelle 
													//cursor
			array = array2;							//setzt beide Arrays gleich, damit es fuer weiter Durchlaeufe 
													//funktioniert
			cursor++;								//erhoeht den cursor um eins
		}
		return array2;								//gibt das erzeugte array zurueck
	}
	
	/**
	 * Gibt die Laenge des Arrays/HashMap zurueck
	 * @return	Die Laenge des Arrays als Integer
	 */
	public int getLength() {
		return map.length;
	}
}
