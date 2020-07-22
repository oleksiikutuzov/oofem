package fem;

import inf.v3d.obj.*;
import inf.v3d.view.*;;

public class Visualiser {
	
	private double displacementScale;
	private double symbolScale;
	private Structure structure;
	private Viewer viewer;
	
	public Visualiser(Structure struct, Viewer viewer) {
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
		
	}
	
	public void drawConstraints() {
		
	}
	
	public void drawElementForces() {
		
	}
	
	public void drawDisplacements() {
		
	}
	
	public void setConstraintSymbolScale() {
		
	}
	
	public void setForceSymbolScale() {
		
	}
	
	public void setForceSymbolRadius() {
		
	}
	
	public void drawForces() {
		
	}
	
	
	

}
