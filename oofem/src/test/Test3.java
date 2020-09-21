package test;

import fem.Element;
import fem.Node;
import iceb.jnumerics.Vector3D;
import inf.text.ArrayFormat;

public class Test3 {

	public static void main(String[] args) {
		
		Node n1 = new Node(3,4,5);
		Node n2 = new Node(6,7,8);
		n1.setPreLoadDisplacement(7,2,9);
		n2.setPreLoadDisplacement(-1,-2,-3);
		
		Element e1 = new Element(21e4,3e-3,n1,n2);

		System.out.print(ArrayFormat.format(e1.getLength()));
		System.out.println(ArrayFormat.format(e1.getCurrentLength()));
		System.out.println("E1 = " + e1.getE1());
		
		e1.computeNonlinearStiffnessMatrix();

	}

}
