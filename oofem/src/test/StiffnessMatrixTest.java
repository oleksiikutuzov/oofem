package test;

import fem.Structure;
//import fem.StandardElement;
import iceb.jnumerics.IMatrix;
import iceb.jnumerics.MatrixFormat;

public class StiffnessMatrixTest {
	public static void main(String[] args) {
		
		Structure struct = new Structure();
		struct.addNode(9.6, 0, -2.5);
		struct.addNode(9.6, 0, 2.5);
		struct.addNode(0,0,0);
		struct.addNode(3.8,6.06,0);
		struct.addElement(21e4, 3e-3, 3, 0);
		struct.addElement(21e4, 3e-3, 3, 1);
		struct.addElement(21e4, 4e-3, 2, 3);
		
		for (int i = 0; i < struct.getNumberOfElements(); i++) {
			System.out.println("Element " + i + " stiffness matrix");
			IMatrix  matr = struct.getElement(i).computeStiffnessMatrix();
			System.out.println(MatrixFormat.format(matr));
			System.out.println();
		}
		
		struct.solve();
	}
}