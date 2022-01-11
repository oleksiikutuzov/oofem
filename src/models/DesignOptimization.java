package models;

import java.util.Arrays;

import fem.Constraint;
import fem.Force;
import fem.Node;
import fem.Structure;
import fem.Visualizer;
import inf.v3d.view.Viewer;

public class DesignOptimization {

	private static double topWidth = 2.0;
	private static double bottomWidth = 4.0;
	private static double totalHeight = 6.0;
	private static int numLevels = 5;
	private static int numRows = 6;
	private static double levelHeight = totalHeight / numLevels;
	private static double atan = (2 * totalHeight) / (bottomWidth - topWidth);
	private static double alpha = Math.atan(atan);
	private static int onLevel = numRows * 4;

	public static void addNodes(Structure struct) {
		double currentLevelHeight;
		double corner;
		double increment;
		for (int level = 0; level < numLevels + 1; level++) {
			currentLevelHeight = levelHeight * level;
			corner = bottomWidth / 2 - currentLevelHeight / Math.tan(alpha);
			increment = 2 * corner / numRows;

			for (int i = 0; i < numRows; i++) {
				struct.addNode(corner, corner - i * increment, currentLevelHeight);
			}
			for (int i = 0; i < numRows; i++) {
				struct.addNode(corner - i * increment, -corner, currentLevelHeight);
			}
			for (int i = 0; i < numRows; i++) {
				struct.addNode(-corner, -corner + i * increment, currentLevelHeight);
			}
			for (int i = 0; i < numRows; i++) {
				struct.addNode(-corner + i * increment, corner, currentLevelHeight);
			}
		}
	}

	public static void addElementsOnLevels(double e, double a, Structure struct) {
		for (int level = 0; level < numLevels + 1; level++) {
			for (int i = 0; i < onLevel - 1; i++) {
				struct.addElement(e, a, i + onLevel * level, (i + onLevel * level) + 1);
			}
			struct.addElement(e, a, 0 + onLevel * level, onLevel + (onLevel * level) - 1);
		}
	}

	public static void addElementsVertical(double e, double a, Structure struct) {
		for (int level = 0; level < numLevels; level++) {
			for (int i = 0; i < onLevel; i++) {
				struct.addElement(e, a, i + onLevel * level, (i + onLevel * level) + onLevel);
			}
		}
	}

	public static void addCrossBraces(double e, double a, Structure struct) {
		for (int level = 0; level < numLevels; level++) {
			int levelOffset = onLevel * level;
			for (int i = 0; i < onLevel - 1; i++) {
				struct.addElement(e, a, i + levelOffset, (i + 1) + onLevel + levelOffset);
				struct.addElement(e, a, (i + 1) + onLevel * level, i + onLevel + levelOffset);
			}
			struct.addElement(e, a, (onLevel - 1) + levelOffset, 0 + onLevel + levelOffset);
			struct.addElement(e, a, 0 + levelOffset, (onLevel - 1) + onLevel + levelOffset);
		}
	}

	public static void addForces(Structure struct, Force force) {
		for (int i = 0; i < onLevel; i++) {
			struct.getNode(numLevels * onLevel + i).setForce(force);
		}
	}
	
	public static void addConstraints(Structure struct) {
		struct.addConstraint(0, false, false, false);
		for (int i = 1; i < onLevel; i++) {
			struct.addConstraint(i, true, true, false);
		}
	}

	public static Structure createStructure() {
		Structure struct = new Structure();
		double lb = 15.0;
		double r = 457.2 / 2000;
		double t = 10.0 / 1000;

		double a = Math.PI * (Math.pow(r, 2) - Math.pow(r - t, 2));
		double a2 = Math.PI * (Math.pow(r / 2, 2) - Math.pow(r / 2 - t / 2, 2));
		double e = 2.1e11;
		Constraint c1 = new Constraint(false, false, false);
		Constraint c2 = new Constraint(true, true, false);

		double totalForce = -12e3;
		double nodeForce = totalForce / (numRows * 4);
		Force f1 = new Force(0, 0, nodeForce);
		

//		System.out.println("Nodal force is " + f1.getComponent(2));

		addNodes(struct);
		addElementsOnLevels(e, a, struct);
		addElementsVertical(e, a, struct);
		addCrossBraces(e, a2, struct);
		addForces(struct, f1);
		addConstraints(struct);


		// create nodes
//		Node n1 = struct.addNode(0.0, 0.0, lb * Math.sqrt(2.0 / 3.0));
//		Node n2 = struct.addNode(0.0, lb / Math.sqrt(3), 0);
//		Node n3 = struct.addNode(-lb / 2, -lb / Math.sqrt(12.0), 0);
//		Node n4 = struct.addNode(lb / 2, -lb / Math.sqrt(12.0), 0);

		// apply BCs
//		n1.setForce(f1);
//		n2.setConstraint(c1);
//		n3.setConstraint(c1);
//		n4.setConstraint(c2);
		// n1.setPreLoadDisplacement(0, 10e-4, 10e-3);

		// create elements
//		struct.addElement(e, a, 0, 1);
//		struct.addElement(e, a, 0, 2);
//		struct.addElement(e, a, 0, 3);
//		struct.addElement(e, a, 1, 2);
//		struct.addElement(e, a, 2, 3);
//		struct.addElement(e, a, 3, 1);

		// struct.printStructure();
		// return the new structure
		return struct;
	}

	public static void main(String[] args) {
		Viewer viewer = new Viewer();
		Structure struct = createStructure();
//		struct.solve();
		struct.printStructure();
//		struct.printResults();
		Visualizer viz = new Visualizer(struct, viewer);

		// calculate radius scale
//		double[] elementsRad = new double[struct.getNumberOfElements()];
//		for (int i = 0; i < struct.getNumberOfElements(); i++) {
//			elementsRad[i] = struct.getElement(i).getArea();
//		}
//		Arrays.sort(elementsRad);
		// System.out.println("Biggest value is " + elementsRad[elementsRad.length -
		// 1]);
		// double radius = Math.sqrt(elementsRad[elementsRad.length - 1] * 4 / 0.014);

		viz.setNodeScale(8e-2);
		viz.drawNodes();
		viz.setRadiusScale(0.5);
		viz.drawElements();
		viz.setConstraintScale(0.4);
		viz.drawConstraints();
		viz.setArrowShaftScale(2e-3);
		viz.setArrowRadiusScale(5e-2);
		viz.drawForces();
//		viz.setDisplacementScale(3e4);
//		viz.drawDisplacements();
//		// viz.setElementForceScale(1e-5);
//		viz.drawElementForces();
		viewer.setVisible(true);

	}

}
