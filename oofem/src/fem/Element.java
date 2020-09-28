package fem;

import iceb.jnumerics.Array2DMatrix;
import iceb.jnumerics.ArrayVector;
import iceb.jnumerics.BLAM;
import iceb.jnumerics.IMatrix;
import iceb.jnumerics.IMatrixRO;
import iceb.jnumerics.IVectorRO;
import inf.text.ArrayFormat;

public class Element {

	private double area;
	private double S11;
	private double eModulus;
	private int[] dofNumbers = new int[6];
	private Node node1;
	private Node node2;
	private double[] displacement = new double[6];

	public Element(double e, double a, Node n1, Node n2) {
		this.area = a;
		this.eModulus = e;
		this.node1 = n1;
		this.node2 = n2;
	}

	public IMatrix computeStiffnessMatrix2() {
		double c1 = (this.getNode2().getPosition().getX1() - this.getNode1().getPosition().getX1()) / this.getLength();
		double c2 = (this.getNode2().getPosition().getX2() - this.getNode1().getPosition().getX2()) / this.getLength();
		double c3 = (this.getNode2().getPosition().getX3() - this.getNode1().getPosition().getX3()) / this.getLength();
		double coeff = this.getEModulus() * this.getArea() / this.getLength();

		IMatrix transform = new Array2DMatrix(2, 6);
		IMatrix k_local = new Array2DMatrix(2, 2);
		IMatrix tmp = new Array2DMatrix(6, 2);
		IMatrix k_global = new Array2DMatrix(6, 6);

		// initialize A and B
		transform.set(0, 0, c1);
		transform.set(0, 1, c2);
		transform.set(0, 2, c3);
		transform.set(1, 3, c1);
		transform.set(1, 4, c2);
		transform.set(1, 5, c3);

		k_local.set(0, 0, coeff * 1);
		k_local.set(0, 1, coeff * -1);
		k_local.set(1, 0, coeff * -1);
		k_local.set(1, 1, coeff * 1);

		// compute
		BLAM.multiply(1.0, BLAM.TRANSPOSE, transform, BLAM.NO_TRANSPOSE, k_local, 0.0, tmp);
		BLAM.multiply(1.0, BLAM.NO_TRANSPOSE, tmp, BLAM.NO_TRANSPOSE, transform, 0.0, k_global);

		// System.out.println(MatrixFormat.format(k_global));

		return k_global;

	}

	public IMatrix computeStiffnessMatrix() {

		// calculate variables
		double c1 = (this.getNode2().getPosition().getX1() - this.getNode1().getPosition().getX1()) / this.getLength();
		double c2 = (this.getNode2().getPosition().getX2() - this.getNode1().getPosition().getX2()) / this.getLength();
		double c3 = (this.getNode2().getPosition().getX3() - this.getNode1().getPosition().getX3()) / this.getLength();
		double coeff = this.getEModulus() * this.getArea() / this.getLength();

		// System.out.println(ArrayFormat.format(new double[]{c1, c2, c3, coeff}) +
		// "\n");

		// initialise matrices
		IMatrix k_glob = new Array2DMatrix(6, 6);
		IMatrix k_part = new Array2DMatrix(3, 3);

		// fill small matrix
		k_part.set(0, 0, coeff * c1 * c1);
		k_part.set(0, 1, coeff * c1 * c2);
		k_part.set(0, 2, coeff * c1 * c3);
		k_part.set(1, 0, coeff * c2 * c1);
		k_part.set(1, 1, coeff * c2 * c2);
		k_part.set(1, 2, coeff * c2 * c3);
		k_part.set(2, 0, coeff * c3 * c1);
		k_part.set(2, 1, coeff * c3 * c2);
		k_part.set(2, 2, coeff * c3 * c3);

		// set parts
		k_glob.setMatrix(0, 0, k_part);
		k_glob.setMatrix(0, 3, k_part.multiply(-1));
		k_glob.setMatrix(3, 0, k_part.multiply(-1));
		k_glob.setMatrix(3, 3, k_part);

		// System.out.println(MatrixFormat.format(k_glob));

		return k_glob;
	}

	public void enumerateDOFs() {
		for (int i = 0; i < 3; i++) {
			this.dofNumbers[i] = this.node1.getDOFNumbers()[i];
			this.dofNumbers[i + 3] = this.node2.getDOFNumbers()[i];
		}
		// System.out.println("DOF Numbers:");
		// System.out.println(ArrayFormat.format(this.getDOFNumbers()));
	}

	public int[] getDOFNumbers() {
		return this.dofNumbers;
	}

	/*
	 * public Vector3D getE1() {
	 * 
	 * }
	 */

	public double getLength() {
		double xLength = node2.getPosition().getX1() - node1.getPosition().getX1();
		double yLength = node2.getPosition().getX2() - node1.getPosition().getX2();
		double zLength = node2.getPosition().getX3() - node1.getPosition().getX3();
		return Math.sqrt(xLength * xLength + yLength * yLength + zLength * zLength);
	}

	public Node getNode1() {
		return this.node1;
	}

	public Node getNode2() {
		return this.node2;
	}

	public double getArea() {
		return this.area;
	}

	public double getEModulus() {
		return this.eModulus;
	}

	public void print() {
		System.out.println(ArrayFormat.format(this.getEModulus()) + ArrayFormat.format(this.getArea())
				+ ArrayFormat.format(this.getLength()));
	}

	public double computeForce() {
		double[] displ = new double[6];
		for (int i = 0; i < 3; i++) {
			displ[i] = this.getNode1().getDisplacement().toArray()[i];
			displ[i + 3] = this.getNode2().getDisplacement().toArray()[i];
		}
		// System.out.println("\nDisplacement Vector");
		// System.out.println(ArrayFormat.format(displacement));

		double c1 = (this.getNode2().getPosition().getX1() - this.getNode1().getPosition().getX1()) / this.getLength();
		double c2 = (this.getNode2().getPosition().getX2() - this.getNode1().getPosition().getX2()) / this.getLength();
		double c3 = (this.getNode2().getPosition().getX3() - this.getNode1().getPosition().getX3()) / this.getLength();
		double coeff = this.getEModulus() * this.getArea() / this.getLength();

		IMatrix a = new Array2DMatrix(6, 1);
		IMatrix c = new Array2DMatrix(2, 1);
		IMatrix transform = new Array2DMatrix(2, 6);
		transform.set(0, 0, c1);
		transform.set(0, 1, c2);
		transform.set(0, 2, c3);
		transform.set(1, 3, c1);
		transform.set(1, 4, c2);
		transform.set(1, 5, c3);

		for (int i = 0; i < displ.length; i++) {
			a.set(i, 0, displ[i]);
		}

		BLAM.multiply(1.0, BLAM.NO_TRANSPOSE, transform, BLAM.NO_TRANSPOSE, a, 0.0, c);

		// System.out.println("\nLocal Displacement Vector");
		// System.out.print(MatrixFormat.format(c));

		double force = coeff * (c.get(1, 0) - c.get(0, 0));

		// System.out.println("\nForce");
		// System.out.print(force);

		return force;
	}

	public void setValues(double e, double a, Node n1, Node n2) {
		this.area = a;
		this.eModulus = e;
		this.node1 = n1;
		this.node2 = n2;
	}

	// Non-linear only

	public IVectorRO create_X() {
		ArrayVector X = new ArrayVector(6);

		for (int i = 0; i < 3; i++) {
			X.set(i, this.getNode1().getPosition().get(i));
			X.set(i + 3, this.getNode2().getPosition().get(i));
		}
		IVectorRO ret = X;
		return ret;
	}

	public IVectorRO create_u() {
		for (int i = 0; i < 6; i++) {
			if (dofNumbers[i] == -1) {
				this.displacement[i] = 0;
			}
		}

		ArrayVector u = new ArrayVector(6);

		for (int i = 0; i < 3; i++) {
			u.set(i, this.displacement[i]);
			u.set(i + 3, this.displacement[i + 3]);
		}
		IVectorRO ret = u;
		return ret;
	}

	public double computeS11(IVectorRO u_e) {
		IVectorRO X = this.create_X();

		double L_square = Math.pow(this.getLength(), 2);
		double l_square = Math.pow(X.get(3) + u_e.get(3) - (X.get(0) + u_e.get(0)), 2)
				+ Math.pow(X.get(4) + u_e.get(4) - (X.get(1) + u_e.get(1)), 2)
				+ Math.pow(X.get(5) + u_e.get(5) - (X.get(2) + u_e.get(2)), 2);

		double S11 = this.eModulus * (l_square - L_square) / (2 * L_square);

		// System.out.println(Math.sqrt(l_square));

		this.S11 = S11;
		return S11;
	}

	public IMatrixRO getAMatrix() {

		IMatrix aMatrix = new Array2DMatrix(6, 6);
		IMatrix aMatrix_part = new Array2DMatrix(3, 3);

		// fill small matrix
		aMatrix_part.set(0, 0, 1);
		aMatrix_part.set(1, 1, 1);
		aMatrix_part.set(2, 2, 1);

		// set parts
		aMatrix.setMatrix(0, 0, aMatrix_part);
		aMatrix.setMatrix(0, 3, aMatrix_part.multiply(-1));
		aMatrix.setMatrix(3, 0, aMatrix_part.multiply(-1));
		aMatrix.setMatrix(3, 3, aMatrix_part);

		IMatrixRO ret = aMatrix;
		return ret;
	}

	public IVectorRO computeInternalForce(int NEQ, double[] u_e_2, int[] DOFNumbers) {
		double[] tmp = new double[6];
		for (int k = 0; k < 6; k++) {
			for (int j = 0; j < NEQ; j++) {
				if (DOFNumbers[k] == j) {
					tmp[k] = u_e_2[j];
				} else if (DOFNumbers[k] == -1) {
					tmp[k] = 0;
				}
			}
		}

		ArrayVector tmp2 = new ArrayVector(tmp);
		IVectorRO u_e = tmp2;

		IVectorRO X = this.create_X();

		IMatrixRO A = this.getAMatrix();
		this.computeS11(u_e);

		IVectorRO delta_E11 = A.multiply(X.add(u_e));
		IVectorRO r = delta_E11.multiply(this.area * S11 / this.getLength());
		return r;
	}

	public IMatrixRO computeTangentMatrix(int NEQ, int[] DOFNumbers, double[] u_e_1) {

		double[] tmp = new double[6];
		for (int k = 0; k < 6; k++) {
			for (int j = 0; j < NEQ; j++) {
				if (DOFNumbers[k] == j) {
					tmp[k] = u_e_1[j];
				} else if (DOFNumbers[k] == -1) {
					tmp[k] = 0;
				}
			}
		}

		ArrayVector tmp2 = new ArrayVector(tmp);
		IVectorRO u_e_2 = tmp2;

		IMatrixRO k_geo = this.getAMatrix().multiply(this.S11 * this.area / this.getLength());

		IVectorRO AmulXu = this.getAMatrix().multiply(this.create_X().add(u_e_2));
		double alpha = eModulus * area / Math.pow(this.getLength(), 3);
		IMatrixRO k_mat = (AmulXu).dyadicProduct(AmulXu).multiply(alpha);
		IMatrixRO k_t = k_geo.add(k_mat);

		return k_t;
	}

	public IVectorRO getDisplacement() {
		ArrayVector disp = new ArrayVector(6);
		disp.assignFrom(this.displacement);
		IVectorRO ret = disp;
		return ret;
	}

	public void setDisplacement(IVectorRO disp) {
		this.displacement = disp.toArray();
	}

}
