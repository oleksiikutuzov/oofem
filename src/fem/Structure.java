package fem;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import iceb.jnumerics.Array2DMatrix;
import iceb.jnumerics.ArrayVector;
import iceb.jnumerics.IMatrix;
import iceb.jnumerics.IVectorRO;
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
	private double[] uInit;
	private double[] viewerScales;

	// add node to structure
	public Node addNode(double x1, double x2, double x3) {
		Node node = new Node(x1, x2, x3);
		this.nodes.add(node);
		return node;
	}

	// add element to structure
	public Element addElement(double e, double a, int n1, int n2) {
		Element element = new Element(e, a, this.getNode(n1), this.getNode(n2));
		this.elements.add(element);
		return element;
	}

	public Force addForce(int id, double r1, double r2, double r3) {
		Force force = new Force(r1, r2, r3);
		this.getNode(id).setForce(force);
		return force;
	}

	public Constraint addConstraint(int id, boolean u1, boolean u2, boolean u3) {
		Constraint constraint = new Constraint(u1, u2, u3);
		this.getNode(id).setConstraint(constraint);
		return constraint;
	}

	// print out information about structure
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
					+ ArrayFormat.format(this.getElement(i).getLength()));
		}
	}

	// print out information about structure and solution to file
	public void writeToFile(String path) throws FileNotFoundException {

		PrintWriter writer = new PrintWriter(path);

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
				writer.println(ArrayFormat.format(i) + " "
						+ ArrayFormat.format(this.getNode(i).getConstraint().getStringArray()));
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
					+ ArrayFormat.format(this.getElement(i).getLength()));
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
		System.out.println("Write to file done");
	}

	// solve system of equations

	public void solve(int steps) {
		// solve nonlinear
		boolean lin = false;

		System.out.println("---------------NEWTON-RAPHSON METHOD---------------");

		// get number of equations
		int NEQ = enumerateDOFs();

		// initialize global stiffness matrix
		this.kGlobal = new Array2DMatrix(NEQ, NEQ);

		// manually for debug
		// steps = 2;

		this.uInit = new double[NEQ];

		double[] update = new double[NEQ];

		/*
		 * this.uInit[0] = 1.183411E-05; this.uInit[1] = -4.750581E-05; this.uInit[2] =
		 * -5.567298E-05; this.uInit[3] = 1.775116E-05; this.uInit[4] = -1.024864E-05;
		 */

		// set initial displacement to update variable
//		this.uInit = update;

		this.applyInitialDisplacements(NEQ, this.uInit);

		for (int n = 0; n < steps; n++) {
			for (int k = 0; k < 20; k++) { // iterations number manually for debug

				if (k == 0) {
					for (int i = 0; i < this.uInit.length; i++) {
						this.uInit[i] = 0;
					}
				}

				System.out.println("\nStep n = " + n + ", k = " + k);
				System.out.println("u_" + n + "_" + k + ArrayFormat.format(this.uInit));

				// initialize global stiffness matrix
				this.kGlobal = new Array2DMatrix(NEQ, NEQ);

				// assemble global stiffness matrix
				assembleStiffnessMatrix(this.kGlobal, lin, this.uInit, NEQ);

				System.out.println("Tangent matrix\n" + MatrixFormat.format(this.kGlobal));

				// internal forces
				double[] intForces = new double[NEQ];
				assembleInternalForceVector(NEQ, intForces, this.uInit);

				// System.out.println("Internal forces\n" + ArrayFormat.format(intForces));

				// external forces

				// initialize global load vector
				this.rGlobal = new double[NEQ];

				// assemble global load vectors
				assembleLoadVector(NEQ, this.rGlobal);

				// update global load vector
				for (int i = 0; i < this.rGlobal.length; i++) {
					this.rGlobal[i] = this.rGlobal[i] * (n + 1);
				}

				System.out.println("External forces \n" + ArrayFormat.format(this.rGlobal));

				// create the solver object
				ILSESolver solver = new GeneralMatrixLSESolver();
				// info object for coefficient matrix
				QuadraticMatrixInfo aInfo = solver.getAInfo();
				// get coefficient matrix
				IMatrix a = solver.getA();
				// right hand side
				double[] b = this.rGlobal;
				// initialize solver
				aInfo.setSize(NEQ);
				solver.initialize();

				// set values to load vector
				for (int i = 0; i < NEQ; i++) {
					for (int j = 0; j < NEQ; j++) {
						a.set(i, j, this.kGlobal.get(i, j));
					}
				}

				/*
				 * System.out.println("\nSolving A x = b"); System.out.println("Matrix A");
				 * System.out.println(MatrixFormat.format(a)); System.out.println("Vector b");
				 * System.out.println(ArrayFormat.format(b));
				 */
				// after calling solve, b contains the solution
				try {
					solver.solve(b);
				} catch (SolveFailedException e) {
					System.out.println("Solve failed: " + e.getMessage());
				}

				// print result
				/*
				 * System.out.println("Solution x"); System.out.println(ArrayFormat.format(b));
				 */

				this.uGlobal = b;
				applyDisplacements(NEQ, this.uGlobal);
				computeForces();

				double[] increment = new double[NEQ];

				for (int i = 0; i < update.length; i++) {
					increment[i] = b[i] - this.uInit[i];
				}

				System.out.println("Increment \n" + ArrayFormat.format(increment));

				update = b;

				System.out.println("Update \n" + ArrayFormat.format(update));

				// calculate error
				ArrayVector vect1 = new ArrayVector(NEQ);
				ArrayVector vect2 = new ArrayVector(NEQ);

				vect1.assignFrom(increment);
				vect2.assignFrom(update);
				if (checkConvergence(vect1, vect2) == true) {
					System.out.printf("Converged at iteration %d \n", k);
					break;
				}

				this.uInit = update;

			}
		}

	}

	public void solve() {

		// linear solution
		boolean lin = true;

		// get number of equations
		int NEQ = enumerateDOFs();
		// initialize global stiffness matrix
		kGlobal = new Array2DMatrix(NEQ, NEQ);
		// assemble global stiffness matrix

		this.assembleStiffnessMatrix(kGlobal, lin, new double[6], NEQ);
		// initialize global load vector
		rGlobal = new double[NEQ];
		// assemble global load vectors
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

		// System.out.println("\nAssembled global force vector");
		// System.out.println(ArrayFormat.format(rGlobal));

		// set values to load vector
		for (int i = 0; i < NEQ; i++) {
			for (int j = 0; j < NEQ; j++) {
				a.set(i, j, this.kGlobal.get(i, j));
			}
		}

		// System.out.println("\nSolving A x = b");
		// System.out.println("Matrix A");
		// System.out.println(MatrixFormat.format(a));
		// System.out.println("Vector b");
		// System.out.println(ArrayFormat.format(b));

		// after calling solve, b contains the solution
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

		// System.out.println("\nAssembled global force matrix");
		// System.out.println(ArrayFormat.format(rGlobal));

		// System.out.println("\nAssembled global matrix");
		// System.out.println(MatrixFormat.format(kGlobal));

	}

	private boolean checkConvergence(IVectorRO vect1, IVectorRO vect2) {
		double eta = 10e-3;
		double err = vect1.normTwo() / vect2.normTwo();
		System.out.println("\nError value: " + err);
		if (eta >= err) {
			return true;
		} else {
			return false;
		}
	}

//	count degrees of freedom
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

	// assembly of a load vector
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

	// assemble internal force vector
	private void assembleInternalForceVector(int NEQ, double[] vect, double[] u_e) {
		for (int i = 0; i < this.getNumberOfElements(); i++) {
			// System.out.println("Internal for element " + i);
			if (this.getElement(i).getInitialDisplacement().toArray() != null) {
				for (int j = 0; j < NEQ; j++) {
					for (int k = 0; k < 6; k++) {
						if (this.getElement(i).getDOFNumbers()[k] == j) {
							vect[j] = this.getElement(i).computeInternalForce(NEQ, this.uInit).get(k);
						}
					}
				}
			}

		}
	}

	// assembly of a stiffness matrix
	private void assembleStiffnessMatrix(IMatrix kGlobal, boolean lin, double[] uInit, int NEQ) {
		for (int i = 0; i < this.getNumberOfElements(); i++) {
			for (int j = 0; j < 6; j++) {
				int n = this.getElement(i).getDOFNumbers()[j];
				if (n >= 0) {
					for (int k = 0; k < 6; k++) {
						int m = this.getElement(i).getDOFNumbers()[k];
						if (m >= 0) {
							if (lin == true) {
								kGlobal.add(n, m, this.getElement(i).computeStiffnessMatrix().get(j, k));
							} else if (lin == false) {
								kGlobal.add(n, m, this.getElement(i).computeTangentMatrix(NEQ, this.uInit).get(j, k));
							}
						}
					}
				}
			}

//			  System.out.println("\nMatrix for element " + i + "\n" + MatrixFormat.format(this.getElement(i).computeTangentMatrix(NEQ, this.uInit)));

		}
	}

	// after solution set the displacements to each node
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
		}
	}

	// after solution set the displacements to each node
	private void applyInitialDisplacements(int NEQ, double[] uGlobal) {
		double[] disp = new double[6];
		for (int i = 0; i < this.getNumberOfElements(); i++) {
			for (int k = 0; k < 6; k++) {
				for (int j = 0; j < NEQ; j++) {
					if (this.getElement(i).getDOFNumbers()[k] == j) {
						disp[k] = uGlobal[j];
					} else if (this.getElement(i).getDOFNumbers()[k] == -1) {
						disp[k] = 0;
					}
				}
			}
			ArrayVector ret = new ArrayVector(6);
			ret.assignFrom(disp);
			// System.out.println(ArrayFormat.format(ret.toArray()));
			this.getElement(i).setInitialDisplacement(ret);
//			System.out.println(
//					"Element " + i + ": " + ArrayFormat.format(this.getElement(i).getInitialDisplacement().toArray()));
		}
	}

	// print out the calculation results
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

	public double getLargestForce() {
		double[] forces = new double[this.getNumberOfElements()];
		for (int i = 0; i < this.getNumberOfElements(); i++) {
			forces[i] = this.getElement(i).computeForce();
		}
		Arrays.sort(forces);
		return forces[forces.length - 1];
	}
	public double getLargestDisplacement() {
	
		double[] displacements = new double[this.getNumberOfNodes() * 3];
		for (int i = 0; i < this.getNumberOfNodes(); i++) {
			displacements[i] = this.getNode(i).getDisplacement().getX1(); 
			displacements[i + 1] = this.getNode(i).getDisplacement().getX2(); 
			displacements[i + 2] = this.getNode(i).getDisplacement().getX3(); 
		}
		Arrays.sort(displacements);
		return displacements[displacements.length - 1];
	}
	
	public void printLargestValues() {		 
		double[] displacements = new double[this.getNumberOfNodes() * 3];
		for (int i = 0; i < this.getNumberOfNodes(); i++) {
			displacements[i] = this.getNode(i).getDisplacement().getX1(); 
			displacements[i + 1] = this.getNode(i).getDisplacement().getX2(); 
			displacements[i + 2] = this.getNode(i).getDisplacement().getX3(); 
		}
		Arrays.sort(displacements);
		
		double[] forces = new double[this.getNumberOfElements()];
		for (int i = 0; i < this.getNumberOfElements(); i++) {
			forces[i] = this.getElement(i).computeForce();
		}
		Arrays.sort(forces);
		
		System.out.println("\nListing largest values");
		System.out.println("Displacements: " + displacements[displacements.length - 1]);
		System.out.println("Forces: " + forces[forces.length - 1]);
	}
	
	public double getStructureWeight() {
		double unitWeight = 7850; // kg/m3
		double totalWeight = 0;
		for (int i = 0; i < this.getNumberOfElements(); i++) {
			totalWeight = totalWeight + this.getElement(i).getArea() * this.getElement(i).getArea() * unitWeight;
		}
		return totalWeight;
	}

	// returns number of Nodes in the structure
	public int getNumberOfNodes() {
		return this.nodes.size();
	}

	// returns the Node object by it's id
	public Node getNode(int id) {
		return this.nodes.get(id);
	}

	// returns number of Elements in the Structure
	public int getNumberOfElements() {
		return this.elements.size();
	}

	// returns the Element object by it's id
	public Element getElement(int id) {
		return this.elements.get(id);
	}

	// returns displacements vector
	public double[] getUGlobal() {
		return this.uGlobal;
	}

	// compute forces in each Element
	public void computeForces() {
		for (int i = 0; i < this.getNumberOfElements(); i++) {
			this.getElement(i).computeForce();
		}
	}

	// get an array of scales values (for import from file)
	public double[] getViewerScales() {
		return viewerScales;
	}

	// set an array of scales values (for import from file)
	public void setViewerScales(double[] viewerScales) {
		this.viewerScales = viewerScales;
	}

	public void editNode(int ind, double x1, double x2, double x3) {
		this.nodes.get(ind).setPosition(x1, x2, x3);
	}

	public void editElement(int ind, double e, double a, int n1, int n2) {
		this.elements.get(ind).setValues(e, a, this.getNode(n1), this.getNode(n2));
	}

	public void deleteElement(int id) {
		this.elements.remove(id);
	}

	public void editForce(int ind, double r1, double r2, double r3) {
		this.nodes.get(ind).getForce().setValues(r1, r2, r3);
	}

	public void editConstraint(int ind, boolean u1, boolean u2, boolean u3) {
		this.nodes.get(ind).getConstraint().setValues(u1, u2, u3);
	}
}
