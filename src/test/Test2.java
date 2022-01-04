package test;

import iceb.jnumerics.Array2DMatrix;
import iceb.jnumerics.IMatrix;
import iceb.jnumerics.MatrixFormat;

public class Test2 {

	public static void main(String[] args) {
		
		IMatrix matrix = new Array2DMatrix(2,2);
		double coeff = 23;
		matrix.add(0, 0, coeff * 1);
		matrix.add(0, 1, coeff * -1);
		matrix.add(1, 0, coeff * -1);
		matrix.add(1, 1, coeff * 1);

		System.out.println(MatrixFormat.format(matrix));
		
	}

}
