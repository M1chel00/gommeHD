package app.exercise.algebra;

/**
 * A Class for Basic Fraction operations
 *
 */
public abstract class BasisFraction implements Fractional{

	/**
	 * Sets Numerator and Denominator
	 * @param numerator new Numerator
	 * @param denominator new Denominator
	 */
	protected abstract void setND(long numerator , long denominator);
	
	/**
	 * Adds the operand to the BasicFraction Element
	 * @param operand summand
	 */
	@Override
	public void add(Fractional operand) {
		long d = this.getD() * operand.getD();		
		long n = this.getD() * operand.getN() + this.getN() * operand.getD();				
		this.setND(n, d);		
	}

	/**
	 * Substract the operand to the BasicFraction Element
	 * @param operand subtrahend
	 */
	@Override
	public void sub(Fractional operand) { 
		this.add(new Rational(operand.getN(), operand.getD()).negation());
	}

	/**
	 * Multiplies the operand with the BasicFraction Element
	 * @param operand factor
	 */
	@Override
	public void mul(Fractional operand) {
		this.setND(this.getN()*operand.getN(), this.getD()*operand.getD());
	}

	/**
	 * Divides the operand with the BasicFraction Element
	 * @param operand divisor
	 */
	@Override
	public void div(Fractional operand) {
		mul(operand.reciprocal());
	}
}
