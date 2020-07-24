package fem;

import java.awt.Color;

import iceb.jnumerics.Vector3D;
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
	private Structure structure;
	private Viewer viewer;
	private double elementForceScale = 2e-6;

	public Visualizer(Structure struct, Viewer viewer) {
		this.setStructure(struct);
		this.setViewer(viewer);
	}

	public void drawElements() {
		CylinderSet cs = new CylinderSet();

		for (int i = 0; i < this.getStructure().getNumberOfElements(); i++) {
			cs.addCylinder(this.getStructure().getElement(i).getNode1().getPosition().toArray(),
					this.getStructure().getElement(i).getNode2().getPosition().toArray(),
					Math.sqrt(this.getStructure().getElement(i).getArea() / Math.PI));
		}
		this.getViewer().addObject3D(cs);
	}

	public void drawConstraints() {
		for (int i = 0; i < this.getStructure().getNumberOfNodes(); i++) {
			if (this.getStructure().getNode(i).getConstraint() != null) {
				for (int j = 0; j < 3; j++) {
					Cone cone = new Cone();
					cone.setHeight(this.constraintScale);
					cone.setRadius(this.constraintScale / 2);
					cone.setColor(new Color(0, 0, 255));
					if (this.getStructure().getNode(i).getConstraint().isFree(j) == false && j == 0) {
						cone.setDirection(1, 0, 0);
						cone.setCenter(this.getStructure().getNode(i).getPosition().getX1() - this.constraintScale,
								this.getStructure().getNode(i).getPosition().getX2(),
								this.getStructure().getNode(i).getPosition().getX3());
						this.getViewer().addObject3D(cone);
					} else if (this.getStructure().getNode(i).getConstraint().isFree(j) == false && j == 1) {
						cone.setDirection(0, 1, 0);
						cone.setCenter(this.getStructure().getNode(i).getPosition().getX1(),
								this.getStructure().getNode(i).getPosition().getX2() - this.constraintScale,
								this.getStructure().getNode(i).getPosition().getX3());
						this.getViewer().addObject3D(cone);
					} else if (this.getStructure().getNode(i).getConstraint().isFree(j) == false && j == 2) {
						cone.setDirection(0, 0, 1);
						cone.setCenter(this.getStructure().getNode(i).getPosition().getX1(),
								this.getStructure().getNode(i).getPosition().getX2(),
								this.getStructure().getNode(i).getPosition().getX3() - this.constraintScale);
						this.getViewer().addObject3D(cone);
					}
				}
			}
		}
	}

	public void drawForces() {
		for (int i = 0; i < this.getStructure().getNumberOfNodes(); i++) {
			if (this.getStructure().getNode(i).getForce() != null) {
				Arrow arrow = new Arrow();
				Vector3D point1 = new Vector3D(
						this.getStructure().getNode(i).getPosition().toArray()[0]
								+ this.getStructure().getNode(i).getForce().getComponent(0) * this.arrowShaftScale * -1,
						this.getStructure().getNode(i).getPosition().getX2()
								+ this.getStructure().getNode(i).getForce().getComponent(1) * this.arrowShaftScale * -1,
						this.getStructure().getNode(i).getPosition().getX3()
								+ this.getStructure().getNode(i).getForce().getComponent(2) * this.arrowShaftScale
										* -1);
				Vector3D point2 = new Vector3D(this.getStructure().getNode(i).getPosition().getX1(),
						this.getStructure().getNode(i).getPosition().getX2(),
						this.getStructure().getNode(i).getPosition().getX3());
				arrow.setRadius(this.arrowRadiusScale);
				arrow.setPoint2(point2.toArray());
				arrow.setPoint1(point1.toArray());
				// System.out.println(MatrixFormat.format(point1));
				// System.out.println(MatrixFormat.format(point2));
				// System.out.println(MatrixFormat.format(this.structure.getNode(i).getForce().toVector3D()));
				arrow.setColor(new Color(255, 0, 0));
				this.getViewer().addObject3D(arrow);

			}
		}
	}

	public void setConstraintScale(double scale) {
		this.constraintScale = scale;
	}

	public void setArrowShaftScale(double scale) {
		this.arrowShaftScale = scale;
	}

	public void setArrowRadiusScale(double scale) {
		this.arrowRadiusScale = scale;
	}

	public void setDisplacementScale(double scale) {
		this.displacementScale = scale;
	}

	public void drawDisplacements() {
		CylinderSet cs = new CylinderSet();

		for (int i = 0; i < this.getStructure().getNumberOfElements(); i++) {
			Vector3D node1 = this.getStructure().getElement(i).getNode1().getPosition().add(
					this.getStructure().getElement(i).getNode1().getDisplacement().multiply(this.displacementScale));
			Vector3D node2 = this.getStructure().getElement(i).getNode2().getPosition().add(
					this.getStructure().getElement(i).getNode2().getDisplacement().multiply(this.displacementScale));

//			System.out.println("Node 1 position before and after displacement");
//			System.out.println(ArrayFormat.format(this.structure.getElement(i).getNode1().getPosition().toArray()));
//			System.out.println(ArrayFormat.format(node1.toArray()));
//			System.out.println("Node 2 position before and after displacement");
//			System.out.println(ArrayFormat.format(this.structure.getElement(i).getNode2().getPosition().toArray()));
//			System.out.println(ArrayFormat.format(node2.toArray()));

			cs.addCylinder(node1.toArray(), node2.toArray(),
					Math.sqrt(this.getStructure().getElement(i).getArea() / Math.PI));
			Color green = new Color(0, 255, 0);
			cs.setColor(green);
		}
		this.getViewer().addObject3D(cs);
	}

	public void drawElementForces() {
		PolygonSet ps = new PolygonSet();
		for (int i = 0; i < this.getStructure().getNumberOfElements(); i++) {
			Vector3D node1 = new Vector3D(this.getStructure().getElement(i).getNode1().getPosition().add(
					this.getStructure().getElement(i).getNode1().getDisplacement().multiply(this.displacementScale)));
			Vector3D node2 = new Vector3D(this.getStructure().getElement(i).getNode2().getPosition().add(
					this.getStructure().getElement(i).getNode2().getDisplacement().multiply(this.displacementScale)));

			Vector3D d_num = node2.subtract(node1);
			double d_den = Math.pow(d_num.normTwo(), -1);
			Vector3D d = d_num.multiply(d_den);

			Vector3D p = d.vectorProduct(node2);
			double mag = Math.signum(this.getStructure().getElement(i).computeForce());
			
			ps.insertVertex(node1.toArray(), mag);
			ps.insertVertex(node2.toArray(), mag);
			ps.insertVertex(node2
					.add(p.multiply(this.elementForceScale).multiply(this.getStructure().getElement(i).computeForce()))
					.toArray(), mag);
			ps.insertVertex(node1
					.add(p.multiply(this.elementForceScale).multiply(this.getStructure().getElement(i).computeForce()))
					.toArray(), mag);
			ps.polygonComplete();


		}
		ps.setColoringByData(true);
		this.getViewer().addObject3D(ps);

	}

	public void setElementForceScale(double scale) {
		this.elementForceScale = scale;
	}

	public Viewer getViewer() {
		return viewer;
	}

	public void setViewer(Viewer viewer) {
		this.viewer = viewer;
	}

	public Structure getStructure() {
		return structure;
	}

	public void setStructure(Structure structure) {
		this.structure = structure;
	}

}
