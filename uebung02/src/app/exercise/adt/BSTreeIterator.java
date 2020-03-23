package app.exercise.adt;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

import app.exercise.visualtree.Node;

/**
 * An Iterator for BSTree class
 * @param <T> Type of the Object
 */
public class BSTreeIterator<T> implements Iterator<T>{

	private Object array[];
	int i = -1;
	
	/**
	 * Creates new Iterator
	 * @param array The array representation in inorder of the Binary Tree
	 */
	public BSTreeIterator(Object array[]) {
		this.array = array;
	}
	
	/**
	 * Checks whether the Iterator has a next element
	 * @return Whether the Iterator has a next element
	 */
	@Override
	public boolean hasNext() { 		
		return (i+1)<array.length;
	}

	/**
	 * Returns the next element
	 * @return The next element
	 */
	@Override
	public T next() {
		if(!this.hasNext()) {
			return null;
		}		
		return (T)array[++i];
	}
}
