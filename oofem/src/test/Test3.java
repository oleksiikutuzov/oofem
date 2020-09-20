package test;

import fem.Element;
import fem.Node;
import iceb.jnumerics.Vector3D;
import inf.text.ArrayFormat;

public class Test3 {

	public static void main(String[] args) {
		
		Node n1 = new Node(3.8,6.06,0);
		Node n2 = new Node(9.6,0,-2.5);
		n1.setPreLoadDispl(1,2,4);
		n2.setPreLoadDispl(3, 2, 1);
		
		Element e1 = new Element(21e4,3e-3,n1,n2);

		System.out.print(ArrayFormat.format(e1.getLength()));
		System.out.print(ArrayFormat.format(e1.getCurrentLength()));

	}

}
