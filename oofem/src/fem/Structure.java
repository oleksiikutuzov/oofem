package fem;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import iceb.jnumerics.Array2DMatrix;
import iceb.jnumerics.ArrayVector;
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
	public void solve(boolean lin) {

		if (lin == true) {
			// linear solution

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

			//System.out.println("\nAssembled global force vector");
			//System.out.println(ArrayFormat.format(rGlobal));

			// set values to load vector
			for (int i = 0; i < NEQ; i++) {
				for (int j = 0; j < NEQ; j++) {
					a.set(i, j, this.kGlobal.get(i, j));
				}
			}

			//System.out.println("\nSolving A x = b");
			//System.out.println("Matrix A");
			//System.out.println(MatrixFormat.format(a));
			//System.out.println("Vector b");
			//System.out.println(ArrayFormat.format(b));

			// after calling solve, b contains the solution
			try {
				solver.solve(b);
			} catch (SolveFailedException e) {
				System.out.println("Solve failed: " + e.getMessage());
			}

			// print result
			//System.out.println("Solution x");
			//System.out.println(ArrayFormat.format(b));

			this.uGlobal = b;
			applyDisplacements(NEQ, this.uGlobal);
			computeForces();

			//System.out.println("\nAssembled global force matrix");
			//System.out.println(ArrayFormat.format(rGlobal));

			//System.out.println("\nAssembled global matrix");
			//System.out.println(MatrixFormat.format(kGlobal));

		} else if (lin == false) {
			// solve nonlinear

			// get number of equations
			int NEQ = enumerateDOFs();
			// initialize global stiffness matrix
			kGlobal = new Array2DMatrix(NEQ, NEQ);

			int steps = 2;

			this.uInit = new double[NEQ];

			/*
			 * double[] update = new double[NEQ]; for (int i = 0; i < update.length; i++) {
			 * update[i] = 0; }
			 */

			this.uInit[0] = 1.183411E-05;
			this.uInit[1] = -4.750581E-05;
			this.uInit[2] = -5.567298E-05;
			this.uInit[3] = 1.775116E-05;
			this.uInit[4] = -1.024864E-05;

			for (int n = 0; n < steps; n++) {
				for (int k = 0; k < 2; k++) {
					// create initial displacements variables for control step

					if (n == 0 && k == 0) {
						for (int i = 0; i < this.getNumberOfElements(); i++) {
							this.getElement(i).setDisplacement(this.getElement(i).create_u());

						}
					}

					// this.uInit = update;
					assembleInitialDisplacementVector(NEQ, this.uInit);

					System.out.println("\nn = " + n + ", k = " + k);

					System.out.println("Update array: " + ArrayFormat.format(this.uInit));

					System.out.println("u_" + n + "_" + k + ArrayFormat.format(this.uInit));

					// assemble global stiffness matrix
					this.assembleStiffnessMatrix(kGlobal, lin, this.uInit, NEQ);

					System.out.println("\nTangent matrix");
					System.out.println(MatrixFormat.format(kGlobal));

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

					System.out.println("External force vector \n " + ArrayFormat.format(rGlobal));

					double[] r_int = new double[NEQ];
					assembleInternalForceVector(NEQ, r_int, this.uInit);
					System.out.println("Internal forces vector \n" + ArrayFormat.format(r_int));

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
					this.uInit = b;

					applyDisplacements(NEQ, this.uGlobal);
					computeForces();

					System.out.println("Increment " + ArrayFormat.format(b));

					ArrayVector tmp = new ArrayVector(NEQ);
					ArrayVector tmp2 = new ArrayVector(NEQ);
					tmp.assignFrom(b);
					tmp2.assignFrom(this.uInit);

					this.uInit = tmp.add(tmp2).toArray();

					this.applyInitialDisplacements(NEQ, this.uInit);

					System.out.println("Update    " + ArrayFormat.format(this.uInit));

					// System.out.println("\nAssembled global force matrix");
					// System.out.println(ArrayFormat.format(rGlobal));

				}
			}

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

	// assemble u_e vector
	private void assembleInitialDisplacementVector(int NEQ, double[] u_e) {
		for (int i = 0; i < this.getNumberOfElements(); i++) {
			if (this.getElement(i).getDisplacement().toArray() != null) {
				for (int j = 0; j < NEQ; j++) {
					for (int k = 0; k < 6; k++) {
						if (this.getElement(i).getDOFNumbers()[k] == j) {
							// u_e.set(j, this.getElement(i).getDisplacement().toArray()[k]);
							u_e[j] = this.getElement(i).getDisplacement().toArray()[k];
						}
					}
				}
			}
		}

	}

	// assemble internal force vector
	private void assembleInternalForceVector(int NEQ, double[] vect, double[] u_e) {
		double[] tmp = new double[6];
		for (int i = 0; i < this.getNumberOfNodes(); i++) {
			for (int k = 0; k < 3; k++) {
				for (int j = 0; j < NEQ; j++) {
					if (this.getElement(i).getDOFNumbers()[k] == j) {
						tmp[k] = u_e[j];
					} else if (this.getElement(i).getDOFNumbers()[k] == -1) {
						tmp[k] = 0;
					}
				}
			}
		}

		for (int i = 0; i < this.getNumberOfElements(); i++) {
			if (this.getElement(i).getDisplacement().toArray() != null) {
				for (int j = 0; j < NEQ; j++) {
					for (int k = 0; k < 6; k++) {
						if (this.getElement(i).getDOFNumbers()[k] == j) {
							vect[j] = this.getElement(i)
									.computeInternalForce(NEQ, tmp, this.getElement(i).getDOFNumbers()).toArray()[k];
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
								kGlobal.add(n, m, this.getElement(i)
										.computeTangentMatrix(NEQ, this.getElement(i).getDOFNumbers(), this.uInit)
										.get(j, k));
							}
						}
					}
				}
			}
			/*
			 * System.out.println("\nMatrix for element " + i);
			 * System.out.println(MatrixFormat.format(this.getElement(i);
			 */
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
			this.getElement(i).setDisplacement(ret);
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

}
