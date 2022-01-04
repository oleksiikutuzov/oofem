package test;

import iceb.jnumerics.IMatrix;
import iceb.jnumerics.MatrixFormat;
import iceb.jnumerics.Vector3D;

public class DyadicProductExample {
	
	public static void main (String[] args ) {
	Vector3D a = new Vector3D(1,5,-2);
	Vector3D b = new Vector3D(-3,8,1);
	IMatrix prod;
	
	prod = a.multiply(2).dyadicProduct(b);
	
	System.out.println("Vector a");
	System.out.println(MatrixFormat.format(a));
	System.out.println("Vector b");
	System.out.println(MatrixFormat.format(b));
	System.out.println("Dyadic product 2a o b");
	System.out.println(MatrixFormat.format(prod));
	
	}
}
