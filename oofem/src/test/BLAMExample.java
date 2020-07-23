package test;

import iceb.jnumerics.Array2DMatrix;
import iceb.jnumerics.BLAM;
import iceb.jnumerics.IMatrix;
import iceb.jnumerics.MatrixFormat;

public class BLAMExample {
	public static void main(String[] args) {
		IMatrix a = new Array2DMatrix(2, 3);
		IMatrix b = new Array2DMatrix(2, 2);
		IMatrix tmp = new Array2DMatrix(3, 2);
		IMatrix c = new Array2DMatrix(3, 3);

		// initialize A and B
		a.set(0, 0, 1);
		a.set(0, 1, -6);
		a.set(0, 2, 3);
		a.set(1, 0, -4);
		a.set(1, 1, 2);
		a.set(1, 2, 5);

		b.set(0, 0, -1);
		b.set(0, 1, 3);
		b.set(1, 0, 9);
		b.set(1, 1, 2);

		// compute 
		BLAM.multiply(1.0, BLAM.TRANSPOSE, a, BLAM.NO_TRANSPOSE, b, 0.0, tmp);
		BLAM.multiply(1.0, BLAM.NO_TRANSPOSE, tmp, BLAM.NO_TRANSPOSE, a, 0.0, c);
		
		// print 
		System.out.println("Matrix A");
		System.out.println(MatrixFormat.format(a));
		System.out.println("Matrix B");
		System.out.println(MatrixFormat.format(b));
		System.out.println("Matrix C = A^T B A");
		System.out.println(MatrixFormat.format(c));
	}
}