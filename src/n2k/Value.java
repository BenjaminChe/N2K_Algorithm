package n2k;

import n2k.Main.Operation;

public class Value {
	
	private double exponent;
	
	private double exponent2;
	
	private int value;
	
	private Operation operation;
	
	public Value(int v) {
		this.value = v;
		this.exponent = 1;
		this.exponent2 = 1;
		operation = null;
	}
	
	public Value(int v, Operation operation) {
		this.value = v;
		this.exponent = 1;
		this.exponent2 = 1;
		this.operation = operation;
	}
	
	public Value(int v, Operation operation, double exponent, double exponent2) {
		this.value = v;
		this.exponent = exponent;
		this.exponent2 = exponent2;
		this.operation = operation;
	}
	
	public double getExponent() {
		return exponent;
	}
	
	public double getExponent2() {
		return exponent2;
	}
	
	public int toInt() {
		return value;
	}
	
	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	
	public Operation getOperation() {
		return operation;
	}

}