package fem;

import java.awt.Color;

import iceb.jnumerics.MatrixFormat;
import iceb.jnumerics.Vector3D;
import inf.text.ArrayFormat;
import inf.v3d.obj.Arrow;
import inf.v3d.obj.Cone;
import inf.v3d.obj.CylinderSet;
import inf.v3d.obj.PolygonSet;
import inf.v3d.view.Viewer;

public class Visualizer {

	private double displacementScale = 1;
	private double constraintScale = 1;
	private double arrowRadiusScale = 1;
	private double arrowShaftScale = 1e-5;
	private double forceScale = 1e-3;
	private Structure structure;
	private Viewer viewer;

	public Visualizer(Structure struct, Viewer viewer) {
		this.structure = struct;
		this.viewer = viewer;
	}

	public void drawElements() {
		CylinderSet cs = new CylinderSet();

		for (int i = 0; i < this.structure.getNumberOfElements(); i++) {
			cs.addCylinder(this.structure.getElement(i).getNode1().getPosition().toArray(),
					this.structure.getElement(i).getNode2().getPosition().toArray(),
					Math.sqrt(this.structure.getElement(i).getArea() / Math.PI));
		}
		this.viewer.addObject3D(cs);
	}

	public void drawConstraints() {
		for (int i = 0; i < this.structure.getNumberOfNodes(); i++) {
			if (this.structure.getNode(i).getConstraint() != null) {
				for (int j = 0; j < 3; j++) {
					Cone cone = new Cone();
					cone.setHeight(this.constraintScale);
					cone.setRadius(this.constraintScale / 2);
					cone.setColor(new Color(0, 0, 255));
					if (this.structure.getNode(i).getConstraint().isFree(j) == false && j == 0) {
						cone.setDirection(1, 0, 0);
						cone.setCenter(this.structure.getNode(i).getPosition().getX1() - this.constraintScale,
								this.structure.getNode(i).getPosition().getX2(),
								this.structure.getNode(i).getPosition().getX3());
						this.viewer.addObject3D(cone);
					} else if (this.structure.getNode(i).getConstraint().isFree(j) == false && j == 1) {
						cone.setDirection(0, 1, 0);
						cone.setCenter(this.structure.getNode(i).getPosition().getX1(),
								this.structure.getNode(i).getPosition().getX2() - this.constraintScale,
								this.structure.getNode(i).getPosition().getX3());
						this.viewer.addObject3D(cone);
					} else if (this.structure.getNode(i).getConstraint().isFree(j) == false && j == 2) {
						cone.setDirection(0, 0, 1);
						cone.setCenter(this.structure.getNode(i).getPosition().getX1(),
								this.structure.getNode(i).getPosition().getX2(),
								this.structure.getNode(i).getPosition().getX3() - this.constraintScale);
						this.viewer.addObject3D(cone);
					}
				}
			}
		}
	}

	public void drawElementForces() {
		for (int i = 0; i < this.structure.getNumberOfNodes(); i++) {
			if (this.structure.getNode(i).getForce() != null) {
				Arrow arrow = new Arrow();
				Vector3D point1 = new Vector3D(
						this.structure.getNode(i).getPosition().toArray()[0]
								+ this.structure.getNode(i).getForce().getComponent(0) * this.arrowShaftScale * -1,
						this.structure.getNode(i).getPosition().getX2()
								+ this.structure.getNode(i).getForce().getComponent(1) * this.arrowShaftScale * -1,
						this.structure.getNode(i).getPosition().getX3()
								+ this.structure.getNode(i).getForce().getComponent(2) * this.arrowShaftScale * -1);
				Vector3D point2 = new Vector3D(this.structure.getNode(i).getPosition().getX1(),
						this.structure.getNode(i).getPosition().getX2(),
						this.structure.getNode(i).getPosition().getX3());
				arrow.setRadius(this.arrowRadiusScale);
				arrow.setPoint2(point2.toArray());
				arrow.setPoint1(point1.toArray());
				// System.out.println(MatrixFormat.format(point1));
				// System.out.println(MatrixFormat.format(point2));
				// System.out.println(MatrixFormat.format(this.structure.getNode(i).getForce().toVector3D()));
				arrow.setColor(new Color(255, 0, 0));
				this.viewer.addObject3D(arrow);

			}
		}
	}

	public void setConstraintSymbolScale(double scale) {
		this.constraintScale = scale;
	}

	public void setForceSymbolScale(double scale) {
		this.arrowShaftScale = scale;
	}

	public void setForceSymbolRadius(double scale) {
		this.arrowRadiusScale = scale;
	}

	public void setDisplacementScale(double scale) {
		this.displacementScale = scale;
	}

	public void drawDisplacements() {
		CylinderSet cs = new CylinderSet();

		for (int i = 0; i < this.structure.getNumberOfElements(); i++) {
			Vector3D node1 = this.structure.getElement(i).getNode1().getPosition()
					.add(this.structure.getElement(i).getNode1().getDisplacement().multiply(this.displacementScale));
			Vector3D node2 = this.structure.getElement(i).getNode2().getPosition()
					.add(this.structure.getElement(i).getNode2().getDisplacement().multiply(this.displacementScale));

//			System.out.println("Node 1 position before and after displacement");
//			System.out.println(ArrayFormat.format(this.structure.getElement(i).getNode1().getPosition().toArray()));
//			System.out.println(ArrayFormat.format(node1.toArray()));
//			System.out.println("Node 2 position before and after displacement");
//			System.out.println(ArrayFormat.format(this.structure.getElement(i).getNode2().getPosition().toArray()));
//			System.out.println(ArrayFormat.format(node2.toArray()));

			cs.addCylinder(node1.toArray(), node2.toArray(),
					Math.sqrt(this.structure.getElement(i).getArea() / Math.PI));
			Color green = new Color(0, 255, 0);
			cs.setColor(green);
		}
		this.viewer.addObject3D(cs);
	}

	public void drawForces() {
		PolygonSet ps = new PolygonSet();
		for (int i = 0; i < this.structure.getNumberOfElements(); i++) {
			Vector3D d_num = (this.structure.getElement(i).getNode2().getPosition()
					.subtract(this.structure.getElement(i).getNode1().getPosition()));
			double d_den = Math.pow(this.structure.getElement(i).getLenght(), -1);
			Vector3D d = new Vector3D(d_num.multiply(d_den));
			Vector3D n1 = new Vector3D(1,-2,3).normalize();
			Vector3D n2 = new Vector3D(-1,3,5).normalize();
			Vector3D p1 = new Vector3D(n1.vectorProduct(d));
			Vector3D p2 = new Vector3D(n2.vectorProduct(d));
			Vector3D s1 = this.structure.getElement(i).getNode1().getPosition().add(p1.multiply(forceScale))
					.multiply(this.structure.getElement(i).computeForce());
			Vector3D s2 = this.structure.getElement(i).getNode2().getPosition().add(p2.multiply(forceScale))
					.multiply(this.structure.getElement(i).computeForce());
			System.out.println(MatrixFormat.format(d_num));
			System.out.println(ArrayFormat.format(d_den));
			System.out.println(MatrixFormat.format(d));
			System.out.println(MatrixFormat.format(n1));
			System.out.println(MatrixFormat.format(p1));
//			System.out.println(MatrixFormat.format(s1));
			System.out.println(MatrixFormat.format(s2));
			
			Arrow arrow = new Arrow();
			arrow.setPoint1(this.structure.getElement(i).getNode1().getPosition().toArray());
			arrow.setPoint2(s2.toArray());
			arrow.setRadius(this.arrowRadiusScale);
			this.viewer.addObject3D(arrow);
			
			ps.insertVertex(this.structure.getElement(i).getNode1().getPosition().toArray(), 1);
			ps.insertVertex(this.structure.getElement(i).getNode2().getPosition().toArray(), 1);
			ps.insertVertex(this.structure.getElement(i).getNode2().getPosition().add(s1).toArray(), 1);
			ps.insertVertex(this.structure.getElement(i).getNode1().getPosition().add(s2).toArray(), 1);
			ps.polygonComplete();
			//this.viewer.addObject3D(ps);
		}
		
		
	}

	public void setForceScale(double scale) {
		this.forceScale = scale;
	}

}
