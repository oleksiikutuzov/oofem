package fem;

import java.util.ArrayList;

import iceb.jnumerics.Array2DMatrix;
import iceb.jnumerics.IMatrix;
import iceb.jnumerics.MatrixFormat;
import inf.text.ArrayFormat;

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

		
		System.out.println("\nConstraints");
		System.out.println(ArrayFormat.iFormat(" node            u1             u2             u3"));
		for (int i = 0; i < this.getNumberOfNodes(); i++) {
			if (this.getNode(i).getConstraint() != null) {
				System.out.println(ArrayFormat.format(i) + ArrayFormat.format(this.getNode(i).getConstraint().getStringArray()));
				}
		}
		
		System.out.println("\nForces");
		System.out.println(ArrayFormat.iFormat(" node            r1             r2             r3"));
		for (int i = 0; i < this.getNumberOfNodes(); i++) {
			if (this.getNode(i).getForce() != null) {
				System.out.println(ArrayFormat.format(i) + ArrayFormat.format(this.getNode(i).getForce().getComponentArray()));
				}
		}
		
		System.out.println("\nElements");
		System.out.println(ArrayFormat.iFormat("  idx             E              A         length"));
		for (int i = 0; i < this.getNumberOfElements(); i++) {
			System.out.println(ArrayFormat.format(i) + ArrayFormat.format(this.getElement(i).getEModulus()) + ArrayFormat.format(this.getElement(i).getArea()) + ArrayFormat.format(this.getElement(i).getLenght()));
		}
	}

	public void solve() {	
		int NEQ = enumerateDOFs();
		IMatrix kGlobal = new Array2DMatrix(NEQ, NEQ);
		assembleStiffnessMatrix(kGlobal);
		
		System.out.println("Assembled global matrix");
		System.out.println(MatrixFormat.format(kGlobal));
		
		
		
	}

	private int enumerateDOFs() {
		int start = 0;
		for (int i = 0; i < this.getNumberOfNodes(); i++) {
			start = this.getNode(i).enumerateDOFs(start);
		}
		for (int k = 0; k < this.getNumberOfElements(); k++) {
			this.getElement(k).enumerateDOFs();
		}
		return start;
	}

	private void assembleLoadVector(double[] rGlobal) {

	}

	private void assembleStiffnessMatrix(IMatrix kGlobal) {
		int NEQ = enumerateDOFs();
		for (int i = 0; i < this.getNumberOfElements(); i++) {
			for (int j = 0; j < NEQ; j++) {
				for (int k = 0; k < NEQ; k++) {
					if (this.getElement(i).getDOFNumbers()[j] != -1 && this.getElement(i).getDOFNumbers()[k] != -1) {
						kGlobal.add(j, k, this.getElement(i).computeStiffnessMatrix().get(j, k));
					}
				}
				
			}
		}
		

	}

	private void selectDisplacements(double[] uGlobal) {
		
	}

	public void printResults() {

	}
}
