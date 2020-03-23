package app.exercise.testing;

import java.util.Scanner;
import java.util.Stack;

import app.exercise.algebra.Rational;

public class RPN {

	public static void main(String[] args) {

		// Saves the numbers
		Stack<Rational> numbers = new Stack<>();

		Scanner in = new Scanner(System.in);
		if (in.hasNext()) {
			// the working line
			String working = in.nextLine();
			// substring from working (equals the next number/operant)
			String sub = "";
			boolean error = false;
			while (working.length() != 0 && error == false) {
				// get new substring

				// last element isnt followed by a whitespace
				if (working.indexOf(' ') < 0) {
					sub = working;
					working = "";
				} else {
					sub = working.substring(0, working.indexOf(' '));
					working = working.substring(working.indexOf(' ') + 1, working.length());
				}

				// check if we found an operant
				if (sub.equals("+")) {
					if (!numbers.empty()) {
						Rational toadd = numbers.pop();
						Rational erg;
						if (!numbers.empty())
							erg = numbers.pop();
						else {
							System.out.println("Error: no numbers left");
							error = true;
							break;
						}
						erg.add(toadd);
						numbers.add(erg);
					}
				} else if (sub.equals("-")) {
					if (!numbers.empty()) {
						Rational toadd = numbers.pop();
						Rational erg;
						if (!numbers.empty())
							erg = numbers.pop();
						else {
							System.out.println("Error: no numbers left");
							error = true;
							break;
						}
						erg.sub(toadd);
						numbers.add(erg);
					}
				} else if (sub.equals("*")) {
					if (!numbers.empty()) {
						Rational toadd = numbers.pop();
						Rational erg;
						if (!numbers.empty())
							erg = numbers.pop();
						else {
							System.out.println("Error: no numbers left");
							error = true;
							break;
						}
						erg.mul(toadd);
						numbers.add(erg);
					}
				} else if (sub.equals("/")) {
					if (!numbers.empty()) {
						Rational toadd = numbers.pop();
						Rational erg;
						if (!numbers.empty())
							erg = numbers.pop();
						else {
							System.out.println("Error: no numbers left");
							error = true;
							break;
						}
						erg.div(toadd);
						numbers.add(erg);
					}
				}
				// else its a number or we have an error
				else {
					try {
						numbers.push(new Rational(Long.parseLong(sub), 1));
					} catch (Exception e) {
						System.out.println("ERROR");
						error = true;
						break;
					}
				}
			}
			if (!error) {
				Rational erg = numbers.pop();
				if (numbers.isEmpty())
					System.out.println(erg);
				else {
					System.out.println("Error: Still numbers left");
					error = true;
				}
			}
			if (error) {
				while (!numbers.isEmpty())
					numbers.pop();
			}

		}
		in.close();
	}
}
