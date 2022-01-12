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
	private static int numLevels = 2;
	private static int numRows = 4;
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
		for (int level = 1; level < numLevels + 1; level++) {
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
	
	public static void addForce(Structure struct, Force force) {
		struct.getNode(struct.getNumberOfNodes() - 1).setForce(force);
	}

	public static void addConstraints(Structure struct) {
		struct.addConstraint(0, false, false, false);
		for (int i = 1; i < onLevel; i++) {
			struct.addConstraint(i, true, true, false);
		}
	}

	public static void addTop(double e, double a, Structure struct) {
		struct.addNode(0, 0, totalHeight + 1);
		for (int i = 0; i < onLevel; i++) {
			struct.addElement(e, a, numRows * 4 * numLevels + i, struct.getNumberOfNodes() - 1);
		}

	}

	public static Structure createStructure() {
		Structure struct = new Structure();
		// value for diameter in cm converted to m
		double r = 20.0 / 200;

		double a = Math.PI * Math.pow(r, 2);
		double a2 = a / 2;
		double e = 2.1e11;

		// In Newtons
		double totalForce = -12e3;
		double nodeForce = totalForce / (numRows * 4);
		Force f1 = new Force(0, 0, nodeForce);

		addNodes(struct);
		addElementsOnLevels(e, a, struct);
		addElementsVertical(e, a, struct);
		addCrossBraces(e, a2, struct);
		addConstraints(struct);
		addTop(e, a, struct);
		addForce(struct, f1);

		// return the new structure
		return struct;
	}

	public static void main(String[] args) {

		Viewer viewer = new Viewer();
		Structure struct = createStructure();
		struct.solve();
		struct.printStructure();
		struct.printResults();
		struct.printLargestValues();

		double steelTensStr = 2.1e6;
		double steelTensStrSF = steelTensStr * 1.2;
		double maxForce = steelTensStrSF * struct.getElement(0).getArea();
		System.out.println("Max force is: " + maxForce);

		Visualizer viz = new Visualizer(struct, viewer);

		viz.setNodeScale(8e-2);
		viz.drawNodes();
		viz.setRadiusScale(1);
		viz.drawElements();
		viz.setConstraintScale(0.4);
		viz.drawConstraints();
		viz.setArrowShaftScale(2e-3);
		viz.setArrowRadiusScale(5e-2);
		viz.drawForces();
		viz.setDisplacementScale(1e-13);
//		viz.drawDisplacements();
//		// viz.setElementForceScale(1e-5);
//		viz.drawElementForces();
		viewer.setVisible(true);

	}

}
