package fem;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import iceb.jnumerics.Array2DMatrix;
import iceb.jnumerics.IMatrix;
import iceb.jnumerics.MatrixFormat;
import iceb.jnumerics.QuadraticMatrixInfo;
import iceb.jnumerics.SolveFailedException;
import iceb.jnumerics.lse.GeneralMatrixLSESolver;
import iceb.jnumerics.lse.ILSESolver;
import inf.text.ArrayFormat;

public class Structure {

	private ArrayList<Node> nodes = new ArrayList<Node>();
	private ArrayList<Element> elements = new ArrayList<Element>();
	private IMatrix kGlobal;
	private double[] rGlobal;
	private double[] uGlobal;
	private String writePath;

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
				System.out.println(
						ArrayFormat.format(i) + ArrayFormat.format(this.getNode(i).getConstraint().getStringArray()));
			}
		}

		System.out.println("\nForces");
		System.out.println(ArrayFormat.iFormat(" node            r1             r2             r3"));
		for (int i = 0; i < this.getNumberOfNodes(); i++) {
			if (this.getNode(i).getForce() != null) {
				System.out.println(
						ArrayFormat.format(i) + ArrayFormat.format(this.getNode(i).getForce().getComponentArray()));
			}
		}

		System.out.println("\nElements");
		System.out.println(ArrayFormat.iFormat("  idx             E              A         length"));
		for (int i = 0; i < this.getNumberOfElements(); i++) {
			System.out.println(ArrayFormat.format(i) + ArrayFormat.format(this.getElement(i).getEModulus())
					+ ArrayFormat.format(this.getElement(i).getArea())
					+ ArrayFormat.format(this.getElement(i).getLenght()));
		}
	}
	
	public void writeToFile() throws FileNotFoundException {
		
		PrintWriter writer = new PrintWriter(writePath);
		
		writer.println("Listing structure\n");
		writer.println("Nodes");
		writer.println(ArrayFormat.iFormat("  idx            x1             x2             x3"));
		for (int i = 0; i < this.getNumberOfNodes(); i++) {
			writer.println(ArrayFormat.format(i) + MatrixFormat.format(this.getNode(i).getPosition()));
		}

		writer.println("\nConstraints");
		writer.println(ArrayFormat.iFormat(" node            u1             u2             u3"));
		for (int i = 0; i < this.getNumberOfNodes(); i++) {
			if (this.getNode(i).getConstraint() != null) {
				writer.println(
						ArrayFormat.format(i) + " " + ArrayFormat.format(this.getNode(i).getConstraint().getStringArray()));
			}
		}

		writer.println("\nForces");
		writer.println(ArrayFormat.iFormat(" node            r1             r2             r3"));
		for (int i = 0; i < this.getNumberOfNodes(); i++) {
			if (this.getNode(i).getForce() != null) {
				writer.println(
						ArrayFormat.format(i) + ArrayFormat.format(this.getNode(i).getForce().getComponentArray()));
			}
		}

		writer.println("\nElements");
		writer.println(ArrayFormat.iFormat("  idx             E              A         length"));
		for (int i = 0; i < this.getNumberOfElements(); i++) {
			writer.println(ArrayFormat.format(i) + ArrayFormat.format(this.getElement(i).getEModulus())
					+ ArrayFormat.format(this.getElement(i).getArea())
					+ ArrayFormat.format(this.getElement(i).getLenght()));
		}
		
		writer.println("\nListing analysis results");
		writer.println("\nDisplacements");
		writer.println(ArrayFormat.iFormat(" node            u1             u2             u3"));
		for (int i = 0; i < this.getNumberOfNodes(); i++) {
			if (this.getNode(i).getDisplacement() != null) {
				writer.println(ArrayFormat.format(i) + MatrixFormat.format(this.getNode(i).getDisplacement()));
			}
		}
		writer.println("\nElement forces");
		writer.println(ArrayFormat.iFormat(" elem         force"));
		for (int i = 0; i < this.getNumberOfElements(); i++) {
			writer.println(ArrayFormat.format(i) + ArrayFormat.format(this.getElement(i).computeForce()));
		}
		
		writer.close();
	}

	public void solve() {
		int NEQ = enumerateDOFs();
		kGlobal = new Array2DMatrix(NEQ, NEQ);
		this.assembleStiffnessMatrix(kGlobal);
		rGlobal = new double[NEQ];
		assembleLoadVector(NEQ, rGlobal);

		// create the solver object
		ILSESolver solver = new GeneralMatrixLSESolver();
		// info object for coefficient matrix
		QuadraticMatrixInfo aInfo = solver.getAInfo();
		// get coefficient matrix
		IMatrix a = solver.getA();
		// right hand side
		double[] b = rGlobal;
		// initialize solver
		aInfo.setSize(NEQ);
		solver.initialize();

		for (int i = 0; i < NEQ; i++) {
			for (int j = 0; j < NEQ; j++) {
				a.set(i, j, this.kGlobal.get(i, j));
			}
		}

		// print
		//System.out.println("\nSolving A x = b");
		//System.out.println("Matrix A");
		//System.out.println(MatrixFormat.format(a));
		// System.out.println("Vector b");
		// System.out.println(ArrayFormat.format(b));

		// after calling solve , b contains the solution
		try {
			solver.solve(b);
		} catch (SolveFailedException e) {
			System.out.println("Solve failed: " + e.getMessage());
		}

		// print result
		// System.out.println("Solution x");
		// System.out.println(ArrayFormat.format(b));

		this.uGlobal = b;
		applyDisplacements(NEQ, this.uGlobal);
		computeForces();

		printStructure();
		printResults();

		// System.out.println("\nAssembled global force matrix");
		// System.out.println(ArrayFormat.format(rGlobal));

		//System.out.println("\nAssembled global matrix");
		//System.out.println(MatrixFormat.format(kGlobal));
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

	private void assembleLoadVector(int NEQ, double[] rGlobal) {
		for (int i = 0; i < this.getNumberOfNodes(); i++) {
			if (this.getNode(i).getForce() != null) {
				for (int j = 0; j < NEQ; j++) {
					for (int k = 0; k < 3; k++) {
						if (this.getNode(i).getDOFNumbers()[k] == j) {
							rGlobal[j] = this.getNode(i).getForce().getComponentArray()[k];
						}
					}
				}
			}
		}
	}

	private void assembleStiffnessMatrix(IMatrix kGlobal) {
		for (int i = 0; i < this.getNumberOfElements(); i++) {
			for (int j = 0; j < 6; j++) {
				int n = this.getElement(i).getDOFNumbers()[j];
				if (n >= 0) {
					for (int k = 0; k < 6; k++) {
						int m = this.getElement(i).getDOFNumbers()[k];
						if (m >= 0) {
							kGlobal.add(n, m, this.getElement(i).computeStiffnessMatrix().get(j, k));
						}
					}
				}
			}
			//System.out.println("\nMatrix for element " + i);
			//System.out.println(MatrixFormat.format(this.getElement(i).computeStiffnessMatrix()));
		}
	}

	private void applyDisplacements(int NEQ, double[] uGlobal) {
		double[] disp = new double[3];
		for (int i = 0; i < this.getNumberOfNodes(); i++) {
			for (int k = 0; k < 3; k++) {
				for (int j = 0; j < NEQ; j++) {
					if (this.getNode(i).getDOFNumbers()[k] == j) {
						disp[k] = this.uGlobal[j];
					} else if (this.getNode(i).getDOFNumbers()[k] == -1) {
						disp[k] = 0;
					}
				}
			}
			this.getNode(i).setDisplacement(disp);
			// System.out.println(ArrayFormat.format(this.getNode(i).getDOFNumbers()));
			// System.out.println(MatrixFormat.format(this.getNode(i).getDisplacement()));
		}
	}

	public void printResults() {
		System.out.println("\nListing analysis results");
		System.out.println("\nDisplacements");
		System.out.println(ArrayFormat.iFormat(" node            u1             u2             u3"));
		for (int i = 0; i < this.getNumberOfNodes(); i++) {
			if (this.getNode(i).getDisplacement() != null) {
				System.out.println(ArrayFormat.format(i) + MatrixFormat.format(this.getNode(i).getDisplacement()));
			}
		}
		System.out.println("\nElement forces");
		System.out.println(ArrayFormat.iFormat(" elem         force"));
		for (int i = 0; i < this.getNumberOfElements(); i++) {
			System.out.println(ArrayFormat.format(i) + ArrayFormat.format(this.getElement(i).computeForce()));
		}
		
	}
	

	public double[] getUGlobal() {
		return this.uGlobal;
	}

	public void computeForces() {
		for (int i = 0; i < this.getNumberOfElements(); i++) {
			this.getElement(i).computeForce();
		}
	}
	
	public void setWritePath(String s) {
		this.writePath = s;
	}
}
