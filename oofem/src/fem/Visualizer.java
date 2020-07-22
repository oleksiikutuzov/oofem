package fem;

import inf.v3d.obj.CylinderSet;

import java.awt.Color;

import inf.v3d.obj.Arrow;
import inf.v3d.obj.Cone;
import inf.v3d.view.Viewer;

public class Visualizer {
	
	private double displacementScale;
	private double constraintScale = 1;
	private double arrowRadiusScale = 1;
	private double arrowShaftScale = 1;
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
					cone.setColor(new Color(0,0,255));
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
				for (int j = 0; j < 3; j++) {
					Arrow arrow = new Arrow();
					arrow.setRadius(arrowRadiusScale);
					arrow.setPoint2(this.structure.getNode(i).getPosition().getX1(),
							this.structure.getNode(i).getPosition().getX2(),
							this.structure.getNode(i).getPosition().getX3());
					arrow.setColor(new Color(255,0,0));
					if (this.structure.getNode(i).getForce().getComponent(j) != 0 && j == 0) {
						arrow.setPoint1(this.structure.getNode(i).getPosition().getX1() + this.arrowShaftScale * Math.signum(this.structure.getNode(i).getForce().getComponentArray()[j]),
								this.structure.getNode(i).getPosition().getX2(),
								this.structure.getNode(i).getPosition().getX3());
						this.viewer.addObject3D(arrow);
					} else if (this.structure.getNode(i).getForce().getComponent(j) != 0 && j == 1) {
						arrow.setPoint1(this.structure.getNode(i).getPosition().getX1(),
								this.structure.getNode(i).getPosition().getX2() + this.arrowShaftScale * Math.signum(this.structure.getNode(i).getForce().getComponentArray()[j]),
								this.structure.getNode(i).getPosition().getX3());
						this.viewer.addObject3D(arrow);
					} else if (this.structure.getNode(i).getForce().getComponent(j) != 0 && j == 2) {
						arrow.setPoint1(this.structure.getNode(i).getPosition().getX1(),
								this.structure.getNode(i).getPosition().getX2(),
								this.structure.getNode(i).getPosition().getX3() + this.arrowShaftScale * Math.signum(this.structure.getNode(i).getForce().getComponentArray()[j]));
						this.viewer.addObject3D(arrow);
					} 
				}
			}
		}
	}
	
	public void drawDisplacements() {
		
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

}
