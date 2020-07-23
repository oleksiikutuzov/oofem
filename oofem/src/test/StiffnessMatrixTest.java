package test;

import fem.Element;
import fem.Node;
import fem.StandardElement;
import iceb.jnumerics.*;

public class StiffnessMatrixTest {
	public static void main(String[] args) {
		Node n1 = new Node(0, 0, 0);
		Node n2 = new Node(1, 1, 1);
		Element e = new StandardElement(3, Math.sqrt(3), n1, n2);
		IMatrix ke = e.computeStiffnessMatrix();
		System.out.println("Element stiffness matrix");
		System.out.println(MatrixFormat.format(ke));
	}
}