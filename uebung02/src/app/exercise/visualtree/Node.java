package app.exercise.visualtree;

import app.exercise.algebra.CompRational;
import app.exercise.algebra.Rational;

/**
 * A Node class for the BSTree class
 * @param <T> The type of the Objects
 */
public class Node<T> implements DrawableTreeElement<T>{

	private Node<T> left = null;
	private Node<T> right = null;
	private T value = null;	
	
	/**
	 * Creates a new Node with the value of value
	 * @param value Value to save in the Node
	 */
	public Node(T value) {		
		this.value = value;
	}	
	
	/**
	 * Sets a new right Node
	 * @param n The new right Node of the current Node
	 */
	public void setRight(Node<T> n) {
		this.right = n;
	}
	
	/**
	 * Sets a new left Node
	 * @param n The new left Node of the current Node
	 */
	public void setLeft(Node<T> n) {
		this.left = n;
	}
	
	/**
	 * Get the left Node of the current Node
	 * @return left The left Node of the current Node
	 */
	@Override
	public Node<T> getLeft() {
		// TODO Auto-generated method stub
		return this.left;
	}
	
	/**
	 * Get the right Node of the current Node
	 * @return right The left Node of the current Node
	 */
	@Override
	public Node<T> getRight() {
		// TODO Auto-generated method stub
		return this.right;
	}

	/**
	 * Unused
	 * @return random true or false
	 */
	@Override
	public boolean isRed() {
		// TODO Auto-generated method stub
		return (int)(Math.random()*10) == (int)(Math.random()*10);
	}

	/**
	 * Get the value of current Node
	 * @return The value of current Node
	 */
	@Override
	public T getValue() {
		// TODO Auto-generated method stub
		return this.value;
	}
	
	/**
	 * Set new value for current Node
	 * @param value The new value of current Node
	 */
	public void setValue(T value) {
		this.value = value;
	}
	
}
