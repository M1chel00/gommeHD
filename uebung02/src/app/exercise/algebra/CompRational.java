package app.exercise.algebra;

/**
 * An extended class of Rational which allows to compare two Rational Numbers
 */
public class CompRational extends Rational implements Comparable<CompRational>{

	/**
	 * Creates new CompRational
	 * @param n Numerator
	 * @param d Denominator
	 */
	public CompRational(long n, long d) {
		super(n, d);
	}

	/**
	 * Compares the Objects numerical. Returns 0 if they are equal, -1 if o is bigger, or  1 if this is bigger
	 * @param o The CompRational Element to check
	 * @return Whether the Rational Objects are numerical equal, one is smaller, or bigger
	 */
	@Override
	public int compareTo(CompRational o) {
		long d = this.getD() * o.getD();
		long this_n = this.getN() * o.getD();
		long o_n = o.getN() * this.getD();
		return this_n > o_n ? 1 : this_n == o_n ? 0 : this_n < o_n ? -1 : null;
	}

}
