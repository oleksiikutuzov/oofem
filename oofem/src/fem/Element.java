package fem;

import iceb.jnumerics.Array2DMatrix;
import iceb.jnumerics.BLAM;
import iceb.jnumerics.IMatrix;
import inf.text.ArrayFormat;

public class Element {

	private double area;
	private double eModulus;
	private int[] dofNumbers = new int[6];
	private Node node1;
	private Node node2;

	public Element(double e, double a, Node n1, Node n2) {
		this.area = a;
		this.eModulus = e;
		this.node1 = n1;
		this.node2 = n2;
	}

	public IMatrix computeStiffnessMatrix2() {
		double c1 = (this.getNode2().getPosition().getX1() - this.getNode1().getPosition().getX1()) / this.getLenght();
		double c2 = (this.getNode2().getPosition().getX2() - this.getNode1().getPosition().getX2()) / this.getLenght();
		double c3 = (this.getNode2().getPosition().getX3() - this.getNode1().getPosition().getX3()) / this.getLenght();
		double coeff = this.getEModulus() * this.getArea() / this.getLenght();

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
		double c1 = (this.getNode2().getPosition().getX1() - this.getNode1().getPosition().getX1()) / this.getLenght();
		double c2 = (this.getNode2().getPosition().getX2() - this.getNode1().getPosition().getX2()) / this.getLenght();
		double c3 = (this.getNode2().getPosition().getX3() - this.getNode1().getPosition().getX3()) / this.getLenght();
		double coeff = this.getEModulus() * this.getArea() / this.getLenght();

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

	public double getLenght() {
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
				+ ArrayFormat.format(this.getLenght()));
	}

	public double computeForce() {
		double[] displacement = new double[6];
		for (int i = 0; i < 3; i++) {
			displacement[i] = this.getNode1().getDisplacement().toArray()[i];
			displacement[i + 3] = this.getNode2().getDisplacement().toArray()[i];
		}
		// System.out.println("\nDisplacement Vector");
		// System.out.println(ArrayFormat.format(displacement));

		double c1 = (this.getNode2().getPosition().getX1() - this.getNode1().getPosition().getX1()) / this.getLenght();
		double c2 = (this.getNode2().getPosition().getX2() - this.getNode1().getPosition().getX2()) / this.getLenght();
		double c3 = (this.getNode2().getPosition().getX3() - this.getNode1().getPosition().getX3()) / this.getLenght();
		double coeff = this.getEModulus() * this.getArea() / this.getLenght();

		IMatrix a = new Array2DMatrix(6, 1);
		IMatrix c = new Array2DMatrix(2, 1);
		IMatrix transform = new Array2DMatrix(2, 6);
		transform.set(0, 0, c1);
		transform.set(0, 1, c2);
		transform.set(0, 2, c3);
		transform.set(1, 3, c1);
		transform.set(1, 4, c2);
		transform.set(1, 5, c3);

		for (int i = 0; i < displacement.length; i++) {
			a.set(i, 0, displacement[i]);
		}

		BLAM.multiply(1.0, BLAM.NO_TRANSPOSE, transform, BLAM.NO_TRANSPOSE, a, 0.0, c);

		// System.out.println("\nLocal Displacement Vector");
		// System.out.print(MatrixFormat.format(c));

		double force = coeff * (c.get(1, 0) - c.get(0, 0));

		// System.out.println("\nForce");
		// System.out.print(force);

		return force;
	}

}
