package fem;

import iceb.jnumerics.MatrixFormat;
import iceb.jnumerics.Vector3D;

public class Node {

	private int[] dofNumbers = new int[3];
	private Vector3D position;
	private Constraint constraint;
	private Force force;
	private Vector3D displacement;
	private Vector3D currentPosition;
	private Vector3D initialDisplacement;

	public Node(double x1, double x2, double x3) {
		this.position = new Vector3D(x1, x2, x3);
	}

	public void setConstraint(Constraint c) {
		this.constraint = c;
	}

	public Constraint getConstraint() {
		return this.constraint;
	}

	public void setForce(Force f) {
		this.force = f;
	}

	public Force getForce() {
		return this.force;
	}

	public int enumerateDOFs(int start) {
		for (int i = 0; i < 3; i++) {
			if (this.getConstraint() != null) {
				if (this.constraint.isFree(i) == false) {
					this.dofNumbers[i] = -1;
				} else {
					this.dofNumbers[i] = start;
					start++;
				}
			} else {
				this.dofNumbers[i] = start;
				start++;
			}
		}
		return start;
	}

	public int[] getDOFNumbers() {
		return this.dofNumbers;
	}

	public Vector3D getPosition() {
		return this.position;
	}

	public void setDisplacement(double[] u) {
		this.displacement = new Vector3D(u);
	}

	public Vector3D getDisplacement() {
		return this.displacement;
	}

	public void print() {
		System.out.println(MatrixFormat.format(this.position));
	}

	public void setPosition(double x1, double x2, double x3) {
		this.position = new Vector3D(x1, x2, x3);
	}

	public Vector3D getPreLoadDispl() {
		return initialDisplacement;
	}

	public void setInitialDisplacement(double x1, double x2, double x3) {
		this.initialDisplacement = new Vector3D(x1, x2, x3);
		this.currentPosition = this.position.add(initialDisplacement);
	}

	public Vector3D getCurrentPosition() {
		if (currentPosition == null) {
			return this.position;
		} else {
			return currentPosition;
		}
	}

}