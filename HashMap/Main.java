/**
 * Die auszufuehrende Klasse dieser Aufgabe.
 * Legt 2 Hashmaps an, die unterschiedlich hashen und bietet die Moeglichkeit anschlieszend anhand von Spielernamen
 * nach ehemaligen Mitspielern zusuchen.
 * @author michel
 *
 */
public class Main {

	//2 neue Hashmaps werden angelegt, beide Speichern als Objekte Player und haben dieselbe Anzahl an Elementen(groesze)
	//mapName hasht nach namen und mapClub nach Club
	private static HashMap<Player> mapName = new HashMap<>(Euro2008.allTeamPlayer.length);
	private static HashMap<Player> mapClub = new HashMap<>(Euro2008.allTeamPlayer.length);
	
	//integer wird angelegt, der die Laenge der Hashmaps speichert
	private static int length = mapName.getLength();
	
	/**
	 * Die auszufuehrende Funktion des kleinen Programms, welche die gestellte Aufgabe abarbeitet
	 * @param args werden nicht beachtet
	 */
	public static void main(String[] args) {
        for(Player p : Euro2008.allTeamPlayer) {	//geht alle Player der Klasse Euro2008 durch   
        	mapName.put(p, hash(p.getName()));		//hast sie einmal nach name und einmal nach Club und fuegt sie
        	mapClub.put(p, hash(p.getClub()));		//der entsprechenden hashmap hinzu
        }
		
        //Testet die Funktion getPlayersFromPlayersClub mit dem Parameter Tranquillo Barnetta
		getPlayersFromPlayersClub("Dimitris Salpingidis");
	}
	
	/**
	 * Funktion, welche einen mitgelieferten String hast und als Integer zurueckibt
	 * @param hashValue	Der zu hashende String
	 * @return	gibt hashValue gehasht als Integer zurueck
	 */
	private static int hash(String hashValue) {
		int index = 0;
		for(int i = 0; i < hashValue.length(); i++) {	//Geht jeden Char des Strings durch
			index += (i+1)*hashValue.charAt(i);			//multipliziert Stelle des chars(+1) mit dem Wert des chars
		}												//und summiert fuer jeden char auf
		index %= length;								//Summe wird mod die Laenge der Hashmap gerechnet
		return index;									//damit nur die emoeglichen Stellen fuer die Hashmap ausgegeben 
	}													//werden
	
	/**
	 * Ausweichfunktion zum Hashen bei Misserfolg der normalen hashfunktion
	 * @param hashValue Integer des Hashwerts
	 * @return	gibt neuen hashwert als Integer aus
	 */
	private static int hash2(int hashValue) {
		return (hashValue+1)%length;		//addiert alten Hashwert plus 1 und mod die Laenge der HashMap
	}
	
	/**
	 * Funktion um von einem Playernamen alle Mitspieler im Verein gefunden und ausgegeben werden
	 * @param playerName	der zusuchende Spielername als String
	 */
	private static void getPlayersFromPlayersClub(String playerName) {
		int hashValue = hash(playerName);		//Spielername wird gehasht und als hashValue gespeichert		
		if(mapName.get(hashValue) == null) {	//prueft, ob der Spielername in der HashMap exitiert
			System.out.println("Spieler " + playerName + " wurde nicht gefunden!");
			return;								//gibt dies aus und beendet die Funktion
		}
		while(!mapName.get(hashValue).getName().equals(playerName)) {	//checkt, ob sich an der Stelle hashValue im 
																//Array tatsaechlich der gesuchte Spieler befindet
			hashValue = hash2(hashValue);						//solange dies nicht der Fall ist, hashValue auf die
																//Ausweichfunktion von hashValue gesetzt
			if(mapName.get(hashValue) == null) {	//prueft, ob der Spielername in der HashMap exitiert
				System.out.println("Spieler " + playerName + " wurde nicht gefunden!");
				return;								//gibt dies aus und beendet die Funktion
			}
		}
		
		Player club = mapName.get(hashValue);		//Player objekt club des gesuchten PlayerName
		System.out.println("[Search] " + club + "\n");		//gibt das zusuchende Player objekt aus
		
		int hashValueClub = hash(club.getClub());	//hasht den zusuchenden Club vom Player
		Player player = mapClub.get(hashValueClub);	//speichert Player objekt der Hashmap an der Stelle hashValueClub
		while(!player.getClub().equals(club.getClub())) {	//solange der Club des Player objekts player nicht gleich  
			player = mapClub.get(hashValueClub = hash2(hashValueClub));	//dem Club des PlayerObjekt club ist, wird neue  
		}											//Position durch die Ausweichfunktion berechnet und Player Objekt 
													//player auf das Objekt an der neuen Stelle in der Hashmap gesetzt
													//-> sucht erstes Objekt in Hashmap mit gesuchten Club		
		
		for (Object p : mapClub.getArray(hashValueClub)) {	//geht alle Elemente von der Stelle hashValueClub in der 
															//HashMap durch, bis zum naechsten null Element
															//liefert alle moeglichen Elemente mit demselben Club wie 
															//gesucht
			if(((Player) p).getClub().equals(club.getClub())) {	//prueft jedes Element, ob es denselben club wie das
																//gesuchte hat
				System.out.println("[Found]" + (Player)p);		//gibt dieses Player objekt aus
			}			
		}
	}
}
