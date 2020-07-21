package fem;

import iceb.jnumerics.*;

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

	public IMatrix computeStiffnessMatrix() {
		IMatrix matrix = new Array2DMatrix(2,2);
		double coeff = this.getEModulus() * this.getArea() / this.getLenght();
		matrix.add(1, 1, coeff * 1);
		matrix.add(1, 2, coeff * -1);
		matrix.add(2, 1, coeff * -1);
		matrix.add(2, 2, coeff * 1);
		return matrix;
	}
	
	// get dofNumbers from Nodes
	public void enumerateDOFs() {
		int start = 0;
		for (int i = 0; i < 3; i++) {
			if (this.node1.getConstraint().isFree(i) == false) {
				this.dofNumbers[2 * i] = -1;
			} else {
				this.dofNumbers[2 * i] = start;
				start++;
			}

			if (this.node2.getConstraint().isFree(i) == false) {
				this.dofNumbers[2 * i + 1] = -1;
			} else {
				this.dofNumbers[2 * i + 1] = start;
				start++;
			}
		}

	}

	public int[] getDOFNumbers() {
		return this.dofNumbers;
	}

/*	public Vector3D getE1() {

	}
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

/*	public void print() {

	}
*/
	
}
