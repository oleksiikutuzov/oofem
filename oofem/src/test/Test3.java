package test;

import fem.Element;
import fem.Node;

public class Test3 {

	public static void main(String[] args) {
		
		Node n1 = new Node(3.8,6.06,0);
		Node n2 = new Node(9.6,0,-2.5);
		Node n3 = new Node(1,0,0);
		
		Element e1 = new Element(21e4,3e-3,n1,n2);
		Element e2 = new Element(20,1,n2,n3);
		Element e3 = new Element(20,1,n1,n3);
		
		e1.computeStiffnessMatrix();
		

	}

}
