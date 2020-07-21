package test;

import fem.Constraint;
import fem.Node;
import fem.Force;
import fem.Element;
import fem.Structure;

public class Test3 {

	public static void main(String[] args) {
		
		Node n1 = new Node(0,0,0);
		Node n2 = new Node(1,1,0);
		Node n3 = new Node(1,0,0);
		
		Element e1 = new Element(20,1,n1,n2);
		Element e2 = new Element(20,1,n2,n3);
		Element e3 = new Element(20,1,n1,n3);
		
		Structure s1 = new Structure 

	}

}
