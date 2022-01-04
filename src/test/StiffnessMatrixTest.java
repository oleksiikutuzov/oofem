package test;

import fem.Structure;
//import fem.StandardElement;
import iceb.jnumerics.IMatrix;
import iceb.jnumerics.MatrixFormat;

public class StiffnessMatrixTest {
	public static void main(String[] args) {
		
		Structure struct = new Structure();
		struct.addNode(0, 0, 0);
		struct.addNode(1, 1, 1);
		struct.addElement(3, Math.sqrt(3), 0,1);
		
		for (int i = 0; i < struct.getNumberOfElements(); i++) {
			System.out.println("Element " + i + " stiffness matrix");
			IMatrix  matr = struct.getElement(i).computeStiffnessMatrix();
			System.out.println(MatrixFormat.format(matr));
			System.out.println();
		}
	
	}
}