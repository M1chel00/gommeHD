package app.exercise.adt;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.stream.Stream;

import app.exercise.visualtree.DrawableTreeElement;
import app.exercise.visualtree.Node;
/**
 * A Binary Tree implementation
 * @param <T> Type of the Binary Tree
 */
public class BSTree<T extends Comparable<T>> extends AbstractCollection<T>{

	private Node<T> node;
	private int size = 0;
	
	/**
	 * Creates an empty Binary Tree
	 */
	public BSTree() {
		this.node = null;
	}
	
	/**
	 * Returns the root Node
	 * @return The root Node
	 */
	public Node<T> getNode(){
		return node;
	}
	
	/**
	 * Returns the size of the Binary Tree
	 * @return The size
	 */
	@Override
	public int size() {
		return size;
	}
	
	/**
	 * Returns an iterator for this Collection
	 * @return An iterator
	 */
	@Override
	public Iterator<T> iterator() {		
		return new BSTreeIterator<T>(this.toArray());
	}
	
	/**
	 * Returns whether this Binary Tree is empty
	 * @return Whether this Binary Tree is empty
	 */
	public boolean isEmpty() {
		return size==0;
	}

	/**
	 * Checks whether this Binary Tree contains Object o
	 * @param o The object to check
	 * @return whether this Binary Tree contains Object o
	 */
	public boolean contains(Object o) {
		if(isEmpty() || o == null) {
			return false;
		}
		DrawableTreeElement<T> node = this.node;
		T ob = null;
		try {
			ob = (T) o;
			node.getValue().compareTo(ob);
		}catch(ClassCastException e) {
			return false;
		}
		while(node != null) {
			if(node.getValue().compareTo(ob) == 0) {
				return true;
			}
			if(node.getValue().compareTo(ob)<0)
            {
                node=node.getRight();
            }
            else if(node.getValue().compareTo(ob)>0)
            {
                node=node.getLeft();
            }
		}
		return false;
	}
	
	/**
     * Checks whether this Binary tree contains all Objects from the Collection c
     * @param c The Collection to check
     * @return whether this Binary tree contains all Objects from the Collection c
     */
	public boolean containsAll(Collection<?> c) {
		Iterator<?> it = c.iterator();
		while(it.hasNext()) {
			if(!contains(it.next())) {
				return false;
			}
		}
		return true;
	}
	
	/**
     * Returns an array representation of this Binary Tree in inorder
     * @return An array representation of this Binary Tree
     */
	public Object[] toArray() {		
		ArrayList<Object> array = new ArrayList<>();
		getAllElements(node,array);
		return array.toArray();
	}
	
	private void getAllElements(DrawableTreeElement<T> node, ArrayList<Object> array)
    {
        if(node!=null)
        {
            getAllElements(node.getLeft(),array);
            array.add(node.getValue());
            getAllElements(node.getRight(),array);
        }
    }
	
	 /**
     * Returns an String representation of this Binary Tree in inorder
     * @return An String representation of this Binary Tree
     */
	public String toString() {
		Object array[] = this.toArray();
		String s = "";
		for(int i = 0; i < array.length; i++) {
			s += (array[i] + " ");
		}
		return s;
	}
	
	/**
     * Adds an Element to the Binary Tree
     * @param t The object to add
     * @return Whether the Object was added or not
     */
	@Override
	public boolean add(T t) {
		if(isEmpty()) {
            this.node = new Node<>(t);
            size++;
            return true;
        }
        Node<T> node = this.node;
        //search for the right position
        while(node!=null)
        {
            //we already have this value
            if(node.getValue().compareTo(t)==0) {
                return false;
            }
            //t is bigger than our node
            if(node.getValue().compareTo(t)<0)
            {
                //add right or continue search
                if(node.getRight()==null)
                {
                    node.setRight(new Node<>(t));
                    size++;
                    return true;
                }
                node=node.getRight();
            }
            //t is smaller than our node
            else if(node.getValue().compareTo(t)>0)
            {
                //add left  or continue search
                if(node.getLeft()==null)
                {
                    node.setLeft(new Node<>(t));
                    size++;
                    return true;
                }
                node=node.getLeft();
            }

        }
        return false;
	}

	/**
     * Removes the Object o from the Binary Tree
     * @param o The Object to delete
     * @return Whether the Object got deleted or not
     */
	@Override
	public boolean remove(Object o) {		
		if(isEmpty() || o == null) {
			return false;
		}		
		try {
			T ob = (T) o;
			node.getValue().compareTo(ob);
			node = deleteRecursive(node, ob);
		}catch(ClassCastException e) {
			return false;
		}catch(NoSuchElementException e) {
			return false;
		}
		return true;
	}
	
	private Node<T> deleteRecursive(Node<T> node, T o)
    {
        //if node is null and we didnt delete so far => element dont exist
        if(node==null)
        {
            throw new NoSuchElementException();
        }
        //go right since o is bigger than our node
        if(node.getValue().compareTo(o)<0)
        {
            node.setRight(deleteRecursive(node.getRight(),o));
        }
        //go left since o is smaller than our node
        else if(node.getValue().compareTo(o)>0) {
            node.setLeft(deleteRecursive(node.getLeft(),o));
        }

        //we found our node to delete
        if(node.getValue().equals(o))
        {
            //leaf => just return null
            if (node.getLeft() == null && node.getRight() == null) {
                return null;
            }
            //if one side is empty
            if (node.getRight() == null) {
                return node.getLeft();
            }
            //or the other one
            if (node.getLeft() == null) {
                return node.getRight();
            }
            //left and right have trees => go right and search for the smallest value
            T small = findSmallestObject(node.getRight());
            node.setValue(small); //replace the values
            node.setRight(deleteRecursive(node.getRight(), small)); //delete the just moved value
            return node;
        }
        return node;
    }
	
    private T findSmallestObject(Node<T> node)
    {
        while(node.getLeft()!=null)
            node=node.getLeft();
        return node.getValue();
    }
    
    /**
     * Unsupported
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported
     */
    @Override
    public <T1> T1[] toArray(T1[] a) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }
}
