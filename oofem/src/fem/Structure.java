package fem;

import iceb.jnumerics.*;
import inf.text.ArrayFormat;

import java.util.*;

public class Structure {

	private ArrayList<Node> nodes = new ArrayList<Node>();
	private ArrayList<Element> elements = new ArrayList<Element>();

	public Node addNode(double x1, double x2, double x3) {
		Node node = new Node(x1, x2, x3);
		this.nodes.add(node);
		return node;
	}

	public Element addElement(double e, double a, int n1, int n2) {
		Element element = new Element(e, a, this.getNode(n1), this.getNode(n2));
		this.elements.add(element);
		return element;
	}

	public int getNumberOfNodes() {
		return this.nodes.size();
	}

	public Node getNode(int id) {
		return this.nodes.get(id);
	}

	public int getNumberOfElements() {
		return this.elements.size();
	}

	public Element getElement(int id) {
		return this.elements.get(id);
	}

	public void printStructure() {
		System.out.println("Listing structure\n");
		System.out.println("Nodes");
		System.out.println(ArrayFormat.iFormat("  idx            x1             x2             x3"));
		for (int i = 0; i < this.getNumberOfNodes(); i++) {
			System.out.println(ArrayFormat.format(i) + MatrixFormat.format(this.getNode(i).getPosition()));
		}

		/*
		 * System.out.println("\nConstraints"); for (int i = 0; i <
		 * this.getNumberOfNodes(); i++) { if (this.getNode(i).getConstraint() == null)
		 * { break; } else {
		 * System.out.println(ArrayFormat.format(this.getNode(i).getConstraint().
		 * getStringArray())); }
		 * 
		 * }
		 */
		
		System.out.println("\nElements");
		System.out.println(ArrayFormat.iFormat("  idx             E              A         length"));
		for (int i = 0; i < this.getNumberOfElements(); i++) {
			System.out.println(ArrayFormat.format(i) + ArrayFormat.format(this.getElement(i).getEModulus()) + ArrayFormat.format(this.getElement(i).getArea()) + ArrayFormat.format(this.getElement(i).getLenght()));
		}
	}

	public void solve() {

	}

	private int enumerateDOFs() {
		int start = 0;
		for (int i = 0; i < this.getNumberOfNodes(); i++) {
			this.getNode(i).enumerateDOFs(start);
		}
		for (int k = 0; k < this.getNumberOfElements(); k++) {
			this.getElement(k).enumerateDOFs();
		}
		return start;
	}

	private void assembleLoadVector(double[] rGlobal) {

	}

	private void assembleStiffnessMatrix(IMatrix kGlobal) {

	}

	private void selectDisplacements(double[] uGlobal) {

	}

	public void printResults() {

	}
}
