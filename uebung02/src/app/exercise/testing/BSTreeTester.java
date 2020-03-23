package app.exercise.testing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import app.exercise.adt.BSTree;
import app.exercise.algebra.CompRational;
import app.exercise.visualtree.Node;
import app.exercise.visualtree.RedBlackTreeDrawer;

public class BSTreeTester {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		ArrayList<Long> numbers = new ArrayList<>();		
		while(in.hasNextLong()) {
			numbers.add(in.nextLong());
		}
		if(numbers.size() % 2 != 0) {
			System.out.println("Ungerade Anzahl an Ganzen Zahlen: " + numbers.size());
			return;
		}
		RedBlackTreeDrawer<CompRational> vis = new RedBlackTreeDrawer<>();
		BSTree<CompRational> tree = new BSTree<>();
		BSTree<CompRational> one = new BSTree<>();
		BSTree<CompRational> two = new BSTree<>();
		for(int i = 0; i < numbers.size(); i+=2) {
			CompRational num = new CompRational(numbers.get(i), numbers.get(i+1));
			tree.add(num);
			vis.draw(tree.getNode());
			if(i % 4 == 0) {
				one.add(num);
			}else {
				two.add(num);
			}
		}
		Iterator<CompRational> it = tree.iterator();
		while(it.hasNext()) {
			System.out.print(it.next() + " ");
		}
		System.out.println();
		it = one.iterator();
		while(it.hasNext()) {
			System.out.print(it.next() + " ");
		}
		System.out.println();
		it = two.iterator();
        while(it.hasNext())
        {
            System.out.print(it.next() + " ");
        }
        System.out.println();
		
        try {
			Thread.sleep(5000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		System.out.println("Sekundaerer 1 Baum enthalten? " + tree.containsAll(one));
		System.out.println("Sekundaerer 2 Baum enthalten? " + tree.containsAll(two));
        if(numbers.size() >= 2)
            System.out.println("Erste Rationale Zahl enthalten? " + tree.contains(new CompRational(numbers.get(0), numbers.get(1))));
        if(numbers.size() >= 4)
            System.out.println("Letze Rationale Zahl enthalten? " + tree.contains(new CompRational(numbers.get(numbers.size()-2), numbers.get(numbers.size()-1))));
        if(numbers.size() >= 2)
        {
            CompRational zahl = new CompRational(numbers.get(0), numbers.get(1));
            System.out.println("Try to remove " + zahl + "\tworked:" + tree.remove(zahl));
            vis.draw(tree.getNode());
        }
        
        try {
			Thread.sleep(5000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        if(numbers.size() >= 4)
        {
            CompRational zahl = new CompRational(numbers.get(numbers.size()-2),numbers.get(numbers.size()-1));
            System.out.println("Try to remove " + zahl + "\tworked:" + tree.remove(zahl));
            vis.draw(tree.getNode());
        }
        
        try {
			Thread.sleep(5000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Object array[] = tree.toArray();
        if(array.length >= 2) {            
            CompRational small = (CompRational) array[0];
            CompRational big = (CompRational) array[array.length - 1];
            
            long d = small.getD() * big.getD();            
            while (d < 100) {
                d = d * 10;
            }           
            long upper = (d / big.getD()) * big.getN();
            long lower = (d / small.getD()) * small.getN();            
            for (int i = 0; i < 100; i++) {
                CompRational zahl = new CompRational(lower + (long) (Math.random() * (upper - lower)), d);                
                if (tree.remove(zahl))
                {
                  vis.draw(tree.getNode());
                  System.out.println("Removed: "+zahl);
                }
            }
        }
        return;
	}
	
}
