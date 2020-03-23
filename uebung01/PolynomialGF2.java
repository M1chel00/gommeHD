public class PolynomialGF2 {

	private boolean[] koeffizienten;
	final static boolean[] ZERO = null;
	final static boolean[] ONE = { true };

	public PolynomialGF2() {		//default Konstruktor, erstellt EINS-Polynom
		this.koeffizienten = ONE;
	}

	public PolynomialGF2(boolean[] koef) {		//Konstruktor mit boolean[] Parameter
		if (koef == null) {						//erstellt Polynom, anhand des Parameters
			this.koeffizienten = null;			//trimmed Array vorher
		} else {
			this.koeffizienten = trim(koef);
		}
	}

	public boolean[] toArray() {				//gibt boolean Array des Polynoms zurück
		return koeffizienten;
	}

	private static boolean[] trim(boolean[] a) {		//geht von hinten das Array durch, solange bis ein Wert true ist und entfernt alle false Werte davor
		int length = a.length - 1;
		while (length >= 0 && a[length] != true) {
			length--;
		}
		boolean[] temp = new boolean[length + 1];
		for (int i = 0; i < length + 1; i++) {
			temp[i] = a[i];
		}
		boolean test = false;							//prüft, ob nur false im Array enthalten ist
		for (int i = 0; i < temp.length; i++) {			//gibt dann leeres Array, der Länge 0 zurück
			if (temp[i] == true) {
				test = true;
			}
		}
		if (!test) {
			return new boolean[0];
		}
		return temp;
	}

	boolean isZero() {									//wenn Array nicht initialisiert oder Länge 0 hat, gibt true zurück
		return this.koeffizienten == null || this.koeffizienten.length == 0;
	}

	boolean isOne() {									//gibt true zurück, wenn Array aus einem Element, welches true ist besteht
		return !isZero() && this.koeffizienten.length == 1 && this.koeffizienten[0] == true;
	}

	@Override
	public PolynomialGF2 clone() {						//gibt neues Polynom mit gleicher koeffizienten Liste zurück
		return new PolynomialGF2(this.koeffizienten);
	}

														//überschreibt indirekt equals aus Object, jedoch anderem Parameter(überladen)
	public boolean equals(PolynomialGF2 p) {			//prüft, ob Polynome gleich sind
		if(this.isZero() || p.isZero()) {				//Nullpolynome wird vorher geprüft, da sonst NullpointerException
			return this.isZero() && p.isZero();
		}
		int length = (p.koeffizienten.length < this.koeffizienten.length) ? (this.koeffizienten.length)
				: (p.koeffizienten.length);				//sucht größere Länge raus
		for (int i = 0; i < length; i++) {				//geht Arrays durch und prüft, ob Elemente gleich sind
			try {										//Fehlermeldung kommt immer, für unterschiedliche Größen der Arrays/Polynome
				if (p.koeffizienten[i] != this.koeffizienten[i]) {
					return false;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {							//Nullpolynom wird vorher geprüft, da sonst Fehlermeldung
		if (this.isZero() || this.koeffizienten.length == 0) {
			return "0";
		}												//geht Array durch, guckt ob Wert true ist, wenn ja wird String um x^i ergänzt
		String s = (this.koeffizienten[0]) ? ("1") : ("0");		//i ist Stelle im Array
		for (int i = 1; i < this.koeffizienten.length; i++) {
			if (this.koeffizienten[i]) {
				s += " + x^" + i;
			}
		}
		return s;
	}

	public int hashCode() {								//Nullpolynom wird vorher geprüft und 0 zurückgegeben
		if(this.isZero()) {
			return 0;
		}
		int t = 0;										//prüft Elemente ob true und addiert 2 hoch stelle im Array
		for (int i = 0; i < this.koeffizienten.length; i++) {
			t += (this.koeffizienten[i]) ? (Math.pow(2, i)) : (0);
		}
		return t;
	}

	public PolynomialGF2 plus(PolynomialGF2 p) {		//Nullpolynom wird vorher geprüft und jeweils andere Element wird zurückgegeben
		if(this.isZero()) {
			return new PolynomialGF2(p.koeffizienten);
		}else if(p.isZero()) {
			return new PolynomialGF2(this.koeffizienten);
		}
		int max = (p.koeffizienten.length < this.koeffizienten.length) ? (this.koeffizienten.length)
				: (p.koeffizienten.length); 			// sucht die größere Länge
		int length = (p.koeffizienten.length > this.koeffizienten.length) ? (this.koeffizienten.length)
				: (p.koeffizienten.length); 			// sucht die kleinere Länge
		boolean[] newP = new boolean[max];				//geht Array bis kürzerer Länge durch, vergleicht beide Werte anhand XOR
		for (int i = 0; i < length; i++) {				//schreibt in neues boolean Array
			if (p.koeffizienten[i] == this.koeffizienten[i]) {
				newP[i] = false;
			} else {
				newP[i] = true;
			}
		}												//wenn ungleiche Länge, werden restliche Werte einfach dem neuen Array zugeschrieben
		if (p.koeffizienten.length != this.koeffizienten.length) {
			int length2 = (p.koeffizienten.length < this.koeffizienten.length) ? (this.koeffizienten.length)
					: (p.koeffizienten.length); // sucht die größere Länge
			for (int i = length; i < length2; i++) {
				newP[i] = (p.koeffizienten.length < this.koeffizienten.length) ? (this.koeffizienten[i])
						: (p.koeffizienten[i]);
			}
		}
		return new PolynomialGF2(newP);					//neues Polynom mit neuem Array erstellt und zurückgegeben
	}

	public PolynomialGF2 times(PolynomialGF2 p) {		//prüft Nullpolynom vorher, da sonst Fehlermeldung mit Länge
		PolynomialGF2 time = new PolynomialGF2(null);
		if(this.isZero()) {
			return time;
		}												//Multiplikation funktioniert durch Addition und shift um Stelle i im Array
		for (int i = 0; i < this.koeffizienten.length; i++) {
			if (this.koeffizienten[i] == true) {
				time = time.plus(p.shift(i));
			}
		}
		return time;
	}

	public int degree() {								//gibt Grad zurück, welches der Länge - 1 entspricht
		return this.koeffizienten.length - 1;			//aufgrund von trim, welches beim Erstellen aufgerufen wird
	}

	public PolynomialGF2 shift(int s) {					//verschiebt ein Polynom um s Stellen ins Größere und füllt mit false auf
		if(this.isZero()) {								//Nullpolynom bleibt immer Nullpolynom
			return this.clone();
		}
		if (s < 0) {									//negative Parameter werden mit NullPolynom zurückgewiesen
			return new PolynomialGF2(null);
		}												//erzeugt boolean Array mit alter Länge +s
		boolean[] temp = new boolean[this.koeffizienten.length + s];
		for (int i = 0; i < s; i++) {					//setzt ersten s Werte auf false (würde auch automatisch passieren)
			temp[i] = false;
		}												//setzt restlichen Elemente auf alten Element, aber um s verschoben
		for (int i = s; i < temp.length; i++) {
			temp[i] = this.koeffizienten[i - s];
		}
		return new PolynomialGF2(temp);
	}

	public PolynomialGF2 mod(PolynomialGF2 p) {			//bei größerem Grad des Parameters wird direkt Element zurückgegeben
		if(this.degree() < p.degree()) {
			return this.clone();
		}												//Modulo funktioniert wie Polynomdivision, nur das uns nur der Rest interessiert
		int temp = this.degree() - p.degree();			//Subtraktion ist in diesem Körper wie die Addition, da Überträge uns nicht interessieren
		PolynomialGF2 m = new PolynomialGF2().shift(temp);		//Einspolynom wird um Differenz der Grade geshiftet
		PolynomialGF2 sum = m.times(p).plus(this);		//multipliziert mit Parameter und danach addiert(subtrahiert)
		return sum.mod(p);								//rekursiv bis Grad p größer als this
	}

	public static void main(String[] args) {			//Testumgebung
		System.out.println("Test 1\n");
		boolean array[] = new boolean[7];
		array[0] = true;
		boolean ipola[] = {true, true, false, true};
		PolynomialGF2 ipol = new PolynomialGF2(ipola);
		System.out.println("i | hash\t| x^i");
		System.out.println("---------------------------");
		for(int i = 0; i < 7; i++) {
			if(i!=0) {
				array[i-1] = false;
				array[i] = true;
			}
			System.out.println(i+" | "+new PolynomialGF2(array).mod(ipol).hashCode() + "\t\t| " + new PolynomialGF2(array).mod(ipol));
		}

		System.out.println("\nTest 2\n");
		System.out.println("  | \t0 \t1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \ta \tb \tc \td \te \tf");
		System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");
		boolean ipola2[] = {true, true, false, true, true, false, false, false, true};
		boolean multi[] = {true, true};
		PolynomialGF2 g = new PolynomialGF2();
		for(int i = 0; i < 16; i++) {
			System.out.print(Integer.toHexString(i)+" |");
			for(int j = 0; j < 16; j++) {
				if(j==0 && i==0) {
				}else {
					g = g.times(new PolynomialGF2(multi));
				}
				System.out.print("\t"+Integer.toHexString(g.mod(new PolynomialGF2(ipola2)).hashCode()));
			}
			System.out.println();
		}
	}
}

