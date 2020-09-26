package fem;

import iceb.jnumerics.Array2DMatrix;
import iceb.jnumerics.BLAM;
import iceb.jnumerics.IMatrix;
import iceb.jnumerics.MatrixFormat;
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

	public double getE1() {
		//System.out.println("Length = " + this.getLength());
		//System.out.println("Current length = " + this.getCurrentLength());
		return 0.5 * ((this.getCurrentLength() * this.getCurrentLength() - this.getLength() * this.getLength())
				/ (this.getLength() * this.getLength()));
	}

	public IMatrix computeNonlinearStiffnessMatrix() {

		// calculate cosines
		double c1 = (this.getNode2().getPosition().getX1() - this.getNode1().getPosition().getX1())
				/ this.getCurrentLength();
		double c2 = (this.getNode2().getPosition().getX2() - this.getNode1().getPosition().getX2())
				/ this.getCurrentLength();
		double c3 = (this.getNode2().getPosition().getX3() - this.getNode1().getCurrentPosition().getX3())
				/ this.getCurrentLength();

		// geometrical stifness matrix
		double coeff_geo = this.getArea() * this.getEModulus() / this.getLength() * this.getE1();

		// initialise matrices
		IMatrix k_geo = new Array2DMatrix(6, 6);
		IMatrix k_geo_part = new Array2DMatrix(3, 3);

		// fill small matrix
		k_geo_part.set(0, 0, coeff_geo * c1 * c1);
		k_geo_part.set(1, 1, coeff_geo * c2 * c2);
		k_geo_part.set(2, 2, coeff_geo * c3 * c3);

		// set parts
		k_geo.setMatrix(0, 0, k_geo_part);
		k_geo.setMatrix(0, 3, k_geo_part.multiply(-1));
		k_geo.setMatrix(3, 0, k_geo_part.multiply(-1));
		k_geo.setMatrix(3, 3, k_geo_part);

		// material stifness matrix
		double a = this.getNode1().getCurrentPosition().getX1() - this.getNode2().getCurrentPosition().getX1();
		double b = this.getNode1().getCurrentPosition().getX2() - this.getNode2().getCurrentPosition().getX2();
		double c = this.getNode1().getCurrentPosition().getX3() - this.getNode2().getCurrentPosition().getX3();
		double coeff_mat = this.getEModulus() * this.getArea()
				/ (this.getLength() * this.getLength() * this.getLength());

		// initialise matrices
		IMatrix k_mat = new Array2DMatrix(6, 6);
		IMatrix k_mat_part = new Array2DMatrix(3, 3);

		// fill small matrix
		k_mat_part.set(0, 0, coeff_mat * c1 * c1 * a * a);
		k_mat_part.set(0, 1, coeff_mat * c1 * c2 * a * b);
		k_mat_part.set(0, 2, coeff_mat * c1 * c3 * a * c);
		k_mat_part.set(1, 0, coeff_mat * c2 * c1 * b * a);
		k_mat_part.set(1, 1, coeff_mat * c2 * c2 * b * b);
		k_mat_part.set(1, 2, coeff_mat * c2 * c3 * b * c);
		k_mat_part.set(2, 0, coeff_mat * c3 * c1 * c * a);
		k_mat_part.set(2, 1, coeff_mat * c3 * c2 * c * b);
		k_mat_part.set(2, 2, coeff_mat * c3 * c3 * c * c);

		// set parts
		k_mat.setMatrix(0, 0, k_mat_part);
		k_mat.setMatrix(0, 3, k_mat_part.multiply(-1));
		k_mat.setMatrix(3, 0, k_mat_part.multiply(-1));
		k_mat.setMatrix(3, 3, k_mat_part);
		
		// add matrices together
		IMatrix k_glob = new Array2DMatrix(6, 6);
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
		k_glob.set(i, j, k_geo.get(i, j) + k_mat.get(i, j));
		}}
		
		//System.out.println(MatrixFormat.format(k_geo));
		//System.out.println(MatrixFormat.format(k_mat));
		//System.out.println(MatrixFormat.format(k_glob));
		
		return k_glob;
				
	}
	
	public IMatrix computeNonlinearStiffnessMatrix2() {

		// calculate cosines
		double c1 = (this.getNode2().getPosition().getX1() - this.getNode1().getPosition().getX1())
				/ this.getCurrentLength();
		double c2 = (this.getNode2().getPosition().getX2() - this.getNode1().getPosition().getX2())
				/ this.getCurrentLength();
		double c3 = (this.getNode2().getPosition().getX3() - this.getNode1().getCurrentPosition().getX3())
				/ this.getCurrentLength();

		// geometrical stifness matrix
		double coeff_geo = this.getArea() * this.getEModulus() * this.getE1() / this.getLength();

		// initialise matrices
		IMatrix k_geo = new Array2DMatrix(6, 6);
		IMatrix k_geo_part = new Array2DMatrix(3, 3);

		// fill small matrix
		k_geo_part.set(0, 0, coeff_geo);
		k_geo_part.set(1, 1, coeff_geo);
		k_geo_part.set(2, 2, coeff_geo);

		// set parts
		k_geo.setMatrix(0, 0, k_geo_part);
		k_geo.setMatrix(0, 3, k_geo_part.multiply(-1));
		k_geo.setMatrix(3, 0, k_geo_part.multiply(-1));
		k_geo.setMatrix(3, 3, k_geo_part);

		// material stifness matrix
		double a = this.getNode1().getCurrentPosition().getX1() - this.getNode2().getCurrentPosition().getX1();
		double b = this.getNode1().getCurrentPosition().getX2() - this.getNode2().getCurrentPosition().getX2();
		double c = this.getNode1().getCurrentPosition().getX3() - this.getNode2().getCurrentPosition().getX3();
		double coeff_mat = this.getEModulus() * this.getArea()
				/ (this.getLength() * this.getLength() * this.getLength());

		// initialise matrices
		IMatrix k_mat = new Array2DMatrix(9, 9);
		IMatrix k_mat_part = new Array2DMatrix(3, 3);

		// fill small matrix
		k_mat_part.set(0, 0, coeff_mat * a * a);
		k_mat_part.set(0, 1, coeff_mat * a * b);
		k_mat_part.set(0, 2, coeff_mat * a * c);
		k_mat_part.set(1, 0, coeff_mat * b * a);
		k_mat_part.set(1, 1, coeff_mat * b * b);
		k_mat_part.set(1, 2, coeff_mat * b * c);
		k_mat_part.set(2, 0, coeff_mat * c * a);
		k_mat_part.set(2, 1, coeff_mat * c * b);
		k_mat_part.set(2, 2, coeff_mat * c * c);

		// set parts
		k_mat.setMatrix(0, 0, k_mat_part);
		k_mat.setMatrix(0, 3, k_mat_part.multiply(-1));
		k_mat.setMatrix(3, 0, k_mat_part.multiply(-1));
		k_mat.setMatrix(3, 3, k_mat_part);
		
		// add matrices together
		IMatrix k_glob = new Array2DMatrix(6, 6);
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
		k_glob.set(i, j, k_geo.get(i, j) + k_mat.get(i, j));
		}}
		
		// transformation matrix matrix
		IMatrix transform = new Array2DMatrix(2, 6);
		IMatrix tmp = new Array2DMatrix(6, 6);
		IMatrix glob = new Array2DMatrix(6, 6);

		// initialize A and B
		transform.set(0, 0, c1);
		transform.set(0, 1, c2);
		transform.set(0, 2, c3);
		transform.set(1, 3, c1);
		transform.set(1, 4, c2);
		transform.set(1, 5, c3);

		// compute 
		BLAM.multiply(1.0, BLAM.TRANSPOSE, transform, BLAM.NO_TRANSPOSE, k_glob, 0.0, tmp);
		BLAM.multiply(1.0, BLAM.NO_TRANSPOSE, tmp, BLAM.NO_TRANSPOSE, transform, 0.0, glob);
		
		
		//System.out.println(MatrixFormat.format(k_geo));
		//System.out.println(MatrixFormat.format(k_mat));
		//System.out.println(MatrixFormat.format(k_glob));
		
		
		return glob;
				
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

	public double getCurrentLength() {
		if (this.getNode1().getPreLoadDispl() == null && this.getNode2().getPreLoadDispl() == null) {
			this.getNode1().setPreLoadDisplacement(0, 0, 0);
			this.getNode2().setPreLoadDisplacement(0, 0, 0);
		} else if (this.getNode2().getPreLoadDispl() == null) {
			this.getNode2().setPreLoadDisplacement(0, 0, 0);
		} else if (this.getNode1().getPreLoadDispl() == null) {
			this.getNode1().setPreLoadDisplacement(0, 0, 0);
		}
		double xCurrentLength = node2.getCurrentPosition().getX1() - node1.getCurrentPosition().getX1();
		double yCurrentLength = node2.getCurrentPosition().getX2() - node1.getCurrentPosition().getX2();
		double zCurrentLength = node2.getCurrentPosition().getX3() - node1.getCurrentPosition().getX3();
		return Math.sqrt(
				xCurrentLength * xCurrentLength + yCurrentLength * yCurrentLength + zCurrentLength * zCurrentLength);
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
		double[] displacement = new double[6];
		for (int i = 0; i < 3; i++) {
			displacement[i] = this.getNode1().getDisplacement().toArray()[i];
			displacement[i + 3] = this.getNode2().getDisplacement().toArray()[i];
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

	public void setValues(double e, double a, Node n1, Node n2) {
		this.area = a;
		this.eModulus = e;
		this.node1 = n1;
		this.node2 = n2;
	}

}
