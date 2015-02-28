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
 * @author Benjamin Che
 */
public class Main {
	
	private static final int[][] permutations = {{0,1,2}, {0,2,1}, {1,0,2}, {1,2,0}, {2,0,1}, {2,1,0}};

	/** SETTINGS */
	private static final int MATCH_LIMIT = 102;
	
	private static final int[] rolls2 = {6, 6, 2};
	
	private static boolean randomRolls = false;
	
	private static String[] outcomes = new String[MATCH_LIMIT];
	
	/** END SETTINGS */
	
	public static void main(String[] args) {
		int[] rolls = new int[3];
		if(randomRolls) {
			for(int o = 0; o < 3; o++) {
				rolls[o] = rollThreeDice(1, 6);
			}
		} else {
			rolls = rolls2;
		}
		for(int i = 0; i < rolls.length; i++) {
			System.out.println("Roll " + (i + 1) + " - " + rolls[i]);
		}
		fillSlots();
		System.out.println();
		/**
		 * Start by knocking out numbers that we can match with simple operation combinations
		 */
		exitFlag:
		for(int j = MATCH_LIMIT; j > 0; j--) { 
			int match = j;
			for(int k = 0; k < permutations.length; k++) {
				for(Value l : doSomething(rolls[permutations[k][0]], rolls[permutations[k][1]])) {
					for(Value m : doSomething(l.toInt(), rolls[permutations[k][2]])) {
						if(m.toInt() == match) {
							outcomes[match - 1] = match + " is the result of " + rolls[permutations[k][0]] + l.getOperation().getName() + " " + rolls[permutations[k][1]] + " " + m.getOperation().getName() + " " + rolls[permutations[k][2]];
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
							outcomes[match - 1] = match + " is the result of " + rollsValue[permutations[k][0]].toInt() + extra1 + " " + l.getOperation().getName() + " " + rollsValue[permutations[k][1]].toInt() + extra2 + " " + m.getOperation().getName() + " " + rollsValue[permutations[k][2]].toInt() + " " + extra3;
							continue exitFlag2;
						}
					}
				}
			}
		}
		for(String s : outcomes) {
			System.out.println(s);
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
	
	private static final double[] exponentOperations = {0,1,2,3,0.5,1.5,2.5,3.5,0.25,0.75,1.25,1.75,0.333,0.666,1.333,1.666,2.333,4,5,6,7,8};

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
		/** Exponent manipulation for division */
		for(double i : exponentOperations) {
			double exponentResultA = Math.pow(a.toInt(), i);
			if(exponentResultA % 1 == 0  && !ignoreFirst) {
				for(double j : exponentOperations) {
					double exponentResultB = Math.pow(b.toInt(), j);
					if(exponentResultB % 1 == 0) {
						toReturn.add(new Value((int)exponentResultA / (int)exponentResultB, Operation.DIVIDE, i, j));
					} else {
						toReturn.add(new Value((int)exponentResultA / b.toInt(), Operation.DIVIDE, i, 1));
					}
				}
			} else {
				for(double j : exponentOperations) {
					double exponentResultB = Math.pow(b.toInt(), j);
					if(exponentResultB % 1 == 0) {
						toReturn.add(new Value(a.toInt() / (int)exponentResultB, Operation.DIVIDE, 1, j));
					} else {
						toReturn.add(new Value(a.toInt() / b.toInt(), Operation.DIVIDE, 1, 1));
					}
				}
			}
		}
		//if((a.toInt() % b.toInt()) == 0) { toReturn.add(new Value(a.toInt()/b.toInt(), Operation.DIVIDE)); }
		return toReturn;
	}
	
	private static Value[] convertToValue(int[] rolls) {
		Value[] toReturn = new Value[3];
		for(int i = 0; i < 3; i++) {
			toReturn[i] = new Value(rolls[i]);
		}
		return toReturn;
	}
	
		public static int rollThreeDice(int min, int max) {

		    // NOTE: Usually this should be a field rather than a method
		    // variable so that it is not re-seeded every call.
		    Random rand = new Random();

		    // nextInt is normally exclusive of the top value,
		    // so add 1 to make it inclusive
		    int randomNum = rand.nextInt((max - min) + 1) + min;

		    return randomNum;
		}

	
	private static void fillSlots() {
		for(int i = 0; i < MATCH_LIMIT; i++) {
			numberSlots.add(i + 1);
		}
	}
	
	private static List<Integer> numberSlots = new ArrayList<Integer>(MATCH_LIMIT);
	
	
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


