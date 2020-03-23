package app.exercise.algebra;

/**
 * Diese Klasse erbt von BasisFraction und bietet alle Rechenmoeglichkeiten fuer Rationale Zahlen
 * und ihre Darstellung.
 */
public class Rational extends BasisFraction{

	/**
	 * Nenner des Bruchs
	 */
	private long numerator = 0;
	
	/*
	 * Zaehler des Bruchs
	 */
	private long denominator = 0; 
	
	/**
	 * Konstruktor zum erstellen einer Rationalen Zahl
	 * @param n Ganzzahl = Nenner
	 * @param d Ganzzahl = Zaehler
	 */
	public Rational(long n, long d) {
		this.setND(n, d);
	}
	
	/**
	 * Gibt groeszten gemeinsamen Teiler von a und b zurueck
	 * @param a Ganzzahl
	 * @param b Ganzzahl
	 * @return groeszte gemeinsame Teiler
	 */
	private long gcd(long a, long b) {
		if(b == 0) {
			return a;
		}
		return gcd(b,a%b);
	}
	
	/**
	 * Hilfsmethode zum korrekten Setzen der negativen Zahl
	 */
	private void pos_denominator() {
		if(this.getD() >= 0) {
			return;
		}
		this.setND(this.getN()*(-1), Math.abs(this.getD()));
	}
	
	/**
	 * Gibt numerator zurueck
	 * @return Nenner
	 */
	@Override
	public long getN() {
		// TODO Auto-generated method stub
		return this.numerator;
	}

	/**
	 * Gibt denominator zurueck
	 * @return Zaehler
	 */
	@Override
	public long getD() {
		// TODO Auto-generated method stub
		return this.denominator;
	}

	/**
	 * Gibt negiertes Object zurueck
	 * @return new additive inverse object
	 */
	@Override
	public Fractional negation() {
		// TODO Auto-generated method stub
		Rational r = new Rational(this.getN()*(-1), this.getD());		
		return r;
	}

	/**
	 * Gib multiplikative Object zurueck
	 * @return new multiplicative inverse object
	 */
	@Override
	public Fractional reciprocal() {
		// TODO Auto-generated method stub
		Rational r = new Rational(this.getD(), this.getN());
		return r;
	}

	/**
	 * setzt numerator und denominator values
	 * @param numerator Ganzzahl 1
	 * @param denominator Ganzzahl 2
	 */
	@Override
	protected void setND(long numerator, long denominator) {
		// TODO Auto-generated method stub
		this.numerator = numerator / this.gcd(numerator, denominator);
		this.denominator = denominator / this.gcd(numerator, denominator);
		this.pos_denominator();
	}
	
	/**
	 * gibt neues Rationales Object mit gleichen Werten zurueck
	 * @return cloned Rational 
	 */
	@Override
	public Rational clone() {		
		Rational r = new Rational(this.numerator, this.denominator);
		return r;
	}
	
	/**
	 * gibt String Darstellung der Rationalen Zahl zurueck
	 * @return String Rational
	 */
	@Override
	public String toString() {
		return this.getN() + "/" + this.getD();
	}
	
	/**
	 * Gibt zurueck, ob Objecte gleich sind
	 * @param c Object
	 * @return equals
	 */
	@Override
	public boolean equals(Object c) {
		if(c instanceof Rational) {
			Rational r = (Rational) c;
			if(this.getD() == r.getD() && this.getN() == r.getN()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * gibt eindeutigen hashCode zurueck
	 * ueberschreibt von Object 
	 * @return HashCode
	 */
	@Override
	public int hashCode() {		
		return (int) (this.getD() ^ this.getN());
	}

}
