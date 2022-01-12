package models;

import fem.Force;
import fem.Structure;
import fem.Visualizer;
import inf.v3d.view.Viewer;

public class DesignOptimization {

	private static double topWidth;
	private static double bottomWidth;
	private static double totalHeight;
	private static int numLevels;
	private static int numRows;
	private static double levelHeight;
	private static int onLevel;
	private static double atan;
	private static double alpha;
	private static double diameter;
	private static double eModulus = 2.1e11;

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

	public static void addInnerBraces(double e, double a, Structure struct) {
		for (int level = 1; level < numLevels; level++) {
			int levelOffset = onLevel * level;
			for (int i = 1; i < numRows; i++) {
				struct.addElement(e, a, i + levelOffset, numRows - i + onLevel / 2 + levelOffset);
				struct.addElement(e, a, i + numRows + levelOffset, 2 * numRows - i + onLevel / 2 + levelOffset);
			}
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
			// struct.addElement(e, a, numRows * 4 * numLevels + i * numRows,
			// struct.getNumberOfNodes() - 1);
			struct.addElement(e, a, numRows * 4 * numLevels + i, struct.getNumberOfNodes() - 1);
		}

	}

	public static Structure createStructure() {
		Structure struct = new Structure();

		double a = Math.PI * Math.pow(diameter, 2) / 4;
		double a2 = a / 1.5;

		// In Newtons
		double force = -12e3;
		Force f = new Force(0, 0, force);

		addNodes(struct);
		addElementsOnLevels(eModulus, a, struct);
		addElementsVertical(eModulus, a, struct);
		addCrossBraces(eModulus, a2, struct);
//		addInnerBraces(eModulus, a2, struct);
		addConstraints(struct);
		addTop(eModulus, a, struct);
		addForce(struct, f);

		// return the new structure
		return struct;
	}

	public static void main(String[] args) {

		System.out.println("levels     rows     max force   crit force        weight      displ");

		for (int j = 1; j < 5; j++) {
			for (int i = 1; i < 5; i++) {
				topWidth = 2.0;
				bottomWidth = 4.0;
				totalHeight = 6.0;
				numLevels = i;
				numRows = j;
				levelHeight = totalHeight / numLevels;
				onLevel = numRows * 4;
				atan = (2 * totalHeight) / (bottomWidth - topWidth);
				alpha = Math.atan(atan);

				// value for diameter in cm converted to m
				diameter = 20.0 / 100;

//			Viewer viewer = new Viewer();
				Structure struct = createStructure();
				struct.solve();
//			struct.printStructure();
//			struct.printResults();
//			struct.printLargestValues();

				double steelTensStr = 2.1e6;
				double steelTensStrSF = steelTensStr * 1.2;
				double critForce = steelTensStrSF * struct.getElement(0).getArea();

				System.out.format("     %d        %d        %.1f      %.1f       %.3f      %.3f %n", numLevels, numRows,
						struct.getLargestForce(), critForce, struct.getStructureWeight(), struct.getLargestDisplacement() * 1000);

//			System.out.format("Max displacement (abs): %.3f mm %n", struct.getLargestDisplacement() * 1000);
//			System.out.format("Max elemental force:    %.3f KN %n", struct.getLargestForce() / 1000);
//			System.out.format("Critical force:         %.3f KN %n", critForce / 1000);
//			System.out.format("Total weight:           %.3f kg %n", struct.getStructureWeight());
//			if (critForce > struct.getLargestForce()) {
//				System.out.println("Structure is safe");
//			} else {
//				System.out.println("Structure isn't safe");
//			}
			}
		}

//		Visualizer viz = new Visualizer(struct, viewer);
//
//		viz.setNodeScale(8e-2);
//		viz.drawNodes();
//		viz.setRadiusScale(0.7);
//		viz.drawElements();
//		viz.setConstraintScale(0.4);
//		viz.drawConstraints();
//		viz.setArrowShaftScale(2e-4);
//		viz.setArrowRadiusScale(5e-2);
//		viz.drawForces();
//		viz.setDisplacementScale(10);
//		viz.drawDisplacements();
//		viz.setElementForceScale(1e-5);
//		viz.drawElementForces();
//		viewer.setVisible(true);

	}

}
