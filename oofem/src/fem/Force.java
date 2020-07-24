package fem;

import iceb.jnumerics.Vector3D;
import inf.text.ArrayFormat;

public class Force {

	private double[] components = new double[3];

	public Force(double r1, double r2, double r3) {
		this.components[0] = r1;
		this.components[1] = r2;
		this.components[2] = r3;
	}

	public double getComponent(int c) {
		return this.components[c];
	}
	
	public double[] getComponentArray() {
		return this.components;
	}

	public void print() {
		System.out.println(ArrayFormat.format(this.components));
	}
	
	public Vector3D toVector3D() {
		return new Vector3D(this.components);
	}

}
