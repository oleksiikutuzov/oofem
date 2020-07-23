package test;

import fem.Structure;
import iceb.jnumerics.MatrixFormat;
import inf.text.ArrayFormat;
import models.SmallTetraeder;

public class EnumerateTest2 {
	public static void main(String[] args) {
		Structure struct = SmallTetraeder.createStructure();
		// solve
		
		struct.solve();
		
		/*for (int i = 0; i < struct.getNumberOfElements(); i++) {
			struct.getElement(i).enumerateDOFs();
			System.out.println("Element " + (i+1));
			System.out.println(ArrayFormat.format(struct.getElement(i).getDOFNumbers()));
			System.out.print(MatrixFormat.format(struct.getElement(i).computeStiffnessMatrix()));
		}*/
		
		
		// print equation numbers
		System.out.println("Node degrees of freedom");
		for (int i = 0; i < struct.getNumberOfNodes(); i++) {
			int[] dofNumbers = struct.getNode(i).getDOFNumbers();
			System.out.println(ArrayFormat.format(dofNumbers));
		}
		System.out.println("Element degrees of freedom");
		for (int i = 0; i < struct.getNumberOfElements(); i++) {
			int[] dofNumbers = struct.getElement(i).getDOFNumbers();
			System.out.println(ArrayFormat.format(dofNumbers));
		}
	}
}