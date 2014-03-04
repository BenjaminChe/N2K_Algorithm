package n2k;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Algorithm designed to recursively solve the N2K challenge 
 * The challenge: National Number Knockout is played on a six by six grid of numbers. 
 * Three dice are rolled. The player must incorporate all three numbers that are rolled, 
 * employing addition, subtraction, multiplication, division, roots, or exponents, 
 * in such a way that the result is a number from one to thirty-six. 
 * All three numbers must be used once, but cannot be used more than once. 
 * @author CHEBA1
 */
public class Main {
	
	private static final int[][] permutations = {{0,1,2}, {0,2,1}, {1,0,2}, {1,2,0}, {2,0,1}, {2,1,0}};

	public static void main(String[] args) {
		int[] rolls = rollThreeDice();
		for(int i = 0; i < rolls.length; i++) {
			System.out.println("Roll " + (i + 1) + " - " + rolls[i]);
		}
		fillSlots();
		System.out.println("Filled in 36 slots from 1-36");
		/**
		 * Start by knocking out numbers that we can match with simple operation combinations
		 */
		exitFlag:
		for(int j = 36; j > 0; j--) {
			int match = j;
			for(int k = 0; k < permutations.length; k++) {
				for(Value l : doSomething(rolls[permutations[k][0]], rolls[permutations[k][1]])) {
					for(Value m : doSomething(l.toInt(), rolls[permutations[k][2]])) {
						if(m.toInt() == match) {
							System.out.println("Match! " + match + " is the result of " + rolls[permutations[k][0]] + " " + l.getOperation().getName() + " " + rolls[permutations[k][1]] + " " + m.getOperation().getName() + " " + rolls[permutations[k][2]]);
							numberSlots.remove(j - 1);
							continue exitFlag;
						}
					}
				}
			}
		}
		/**
		 * Proceed to applying exponential operations to force remaining matches
		 */
		Value[] rollsValue = convertToValue(rolls);
		exitFlag2:
		for(int i : numberSlots) {
			int match = i;
			int[][] permutations = {{0,1,2}, {0,2,1}, {1,0,2}, {1,2,0}, {2,0,1}, {2,1,0}};
			for(int k = 0; k < permutations.length; k++) {
				for(Value l : doSomething(rollsValue[permutations[k][0]], rollsValue[permutations[k][1]], false)) {
					for(Value m : doSomething(l, rollsValue[permutations[k][2]], true)) {
						if(m.toInt() == match) {
							String extra1 = (l.getExponent() != 1) ? (" to the " + l.getExponent() + " power ") : "";
							String extra2 = (l.getExponent2() != 1) ? (" to the " + l.getExponent2() + " power ") : "";
							String extra3 = (m.getExponent2() != 1) ? (" to the " + m.getExponent2() + " power ") : "";
							System.out.println("Match! " + match + " is the result of " + rollsValue[permutations[k][0]].toInt() + extra1 + " " + l.getOperation().getName() + " " + rollsValue[permutations[k][1]].toInt() + extra2 + " " + m.getOperation().getName() + " " + rollsValue[permutations[k][2]].toInt() + extra3);
							continue exitFlag2;
						}
					}
				}
			}
			System.out.println("Missed this - " + i);
		}
	}
	
	private static List<Value> doSomething(int a, int b) {
		List<Value> toReturn = new ArrayList<Value>();
		toReturn.add(new Value(a + b, Operation.ADD));
		toReturn.add(new Value(a - b, Operation.SUBTRACT));
		toReturn.add(new Value(a * b, Operation.MULTIPLY));
		if((a % b) == 0) { toReturn.add(new Value(a/b, Operation.DIVIDE)); }
		return toReturn;
	}
	
	private static final double[] exponentOperations = {0.5, 0.25, 0.333, 0, 2, 3, 4, 5, 6};
	
	private static List<Value> doSomething(Value a, Value b, boolean ignoreFirst) {
		List<Value> toReturn = new ArrayList<Value>();
		/** Exponent manipulation for addition */
		for(double i : exponentOperations) {
			double exponentResultA = Math.pow(a.toInt(), i);
			if(exponentResultA % 1 == 0 && !ignoreFirst) {
				for(double j : exponentOperations) {
					double exponentResultB = Math.pow(b.toInt(), j);
					if(exponentResultB % 1 == 0) {
						toReturn.add(new Value((int)exponentResultA + (int)exponentResultB, Operation.ADD, i, j));
					} else {
						toReturn.add(new Value((int)exponentResultA + b.toInt(), Operation.ADD, i, 1));
					}
				}
			} else {
				for(double j : exponentOperations) {
					double exponentResultB = Math.pow(b.toInt(), j);
					if(exponentResultB % 1 == 0) {
						toReturn.add(new Value(a.toInt() + (int)exponentResultB, Operation.ADD, 1, j));
					} else {
						toReturn.add(new Value(a.toInt() + b.toInt(), Operation.ADD, 1, 1));
					}
				}
			}
		}
		/** Exponent manipulation for subtraction */
		for(double i : exponentOperations) {
			double exponentResultA = Math.pow(a.toInt(), i);
			if(exponentResultA % 1 == 0  && !ignoreFirst) {
				for(double j : exponentOperations) {
					double exponentResultB = Math.pow(b.toInt(), j);
					if(exponentResultB % 1 == 0) {
						toReturn.add(new Value((int)exponentResultA - (int)exponentResultB, Operation.SUBTRACT, i, j));
					} else {
						toReturn.add(new Value((int)exponentResultA - b.toInt(), Operation.SUBTRACT, i, 1));
					}
				}
			} else {
				for(double j : exponentOperations) {
					double exponentResultB = Math.pow(b.toInt(), j);
					if(exponentResultB % 1 == 0) {
						toReturn.add(new Value(a.toInt() - (int)exponentResultB, Operation.SUBTRACT, 1, j));
					} else {
						toReturn.add(new Value(a.toInt() - b.toInt(), Operation.SUBTRACT, 1, 1));
					}
				}
			}
		}
		/** Exponent manipulation for multiplication */
		for(double i : exponentOperations) {
			double exponentResultA = Math.pow(a.toInt(), i);
			if(exponentResultA % 1 == 0  && !ignoreFirst) {
				for(double j : exponentOperations) {
					double exponentResultB = Math.pow(b.toInt(), j);
					if(exponentResultB % 1 == 0) {
						toReturn.add(new Value((int)exponentResultA * (int)exponentResultB, Operation.MULTIPLY, i, j));
					} else {
						toReturn.add(new Value((int)exponentResultA * b.toInt(), Operation.MULTIPLY, i, 1));
					}
				}
			} else {
				for(double j : exponentOperations) {
					double exponentResultB = Math.pow(b.toInt(), j);
					if(exponentResultB % 1 == 0) {
						toReturn.add(new Value(a.toInt() * (int)exponentResultB, Operation.MULTIPLY, 1, j));
					} else {
						toReturn.add(new Value(a.toInt() * b.toInt(), Operation.MULTIPLY, 1, 1));
					}
				}
			}
		}
		/** TODO: Exponent manipulation for division */
		if((a.toInt() % b.toInt()) == 0) { toReturn.add(new Value(a.toInt()/b.toInt(), Operation.DIVIDE)); }
		return toReturn;
	}
	
	private static Value[] convertToValue(int[] rolls) {
		Value[] toReturn = new Value[3];
		for(int i = 0; i < 3; i++) {
			toReturn[i] = new Value(rolls[i]);
		}
		return toReturn;
	}
	
	private static int[] rollThreeDice() {
		Random r = new Random();
		int[] rolls = new int[3];
		for(int i = 0; i < 3; i++) {
			rolls[i] = (r.nextInt(6) % 6) + 1;
		}
		return rolls;
	}
	
	private static void fillSlots() {
		for(int i = 0; i < 36; i++) {
			numberSlots.add(i + 1);
		}
	}
	
	private static List<Integer> numberSlots = new ArrayList<Integer>(36);
	
	
	public enum Operation {
		ADD("add by"),
		SUBTRACT("subtract by"),
		MULTIPLY("multiply by"),
		DIVIDE("divide by");
		
		private String name;
		
		Operation(String s) {
			this.name = s;
		}
		
		public String getName() {
			return name;
		}
	}
}


