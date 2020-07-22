package fem;


import inf.text.ArrayFormat;

public class Constraint {

	private boolean[] free = new boolean[3];

	public Constraint(boolean u1, boolean u2, boolean u3) {
		this.free[0] = u1;
		this.free[1] = u2;
		this.free[2] = u3;
	}

	public boolean isFree(int c) {
		return this.free[c];
	}
	
	public String[] getStringArray() {
		String[] string = new String[this.free.length];
		for (int i = 0; i < this.free.length; i++) {
			if (this.free[i] == true) {
				string[i] = "          free";
			} else {
				string[i] = "         fixed";
			}
		}
		return string;
	}

	public void print() {
		System.out.println(ArrayFormat.format(this.getStringArray()));
	}

}
