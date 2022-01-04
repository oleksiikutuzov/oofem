package models;

import java.util.Arrays;

import fem.Constraint;
import fem.Force;
import fem.Node;
import fem.Structure;
import fem.Visualizer;
import inf.v3d.view.Viewer;

public class SmallTetraeder {

	public static Structure createStructure() {
		Structure struct = new Structure();
		double lb = 15.0;
		double r = 457.2 / 2000;
		double t = 10.0 / 1000;
		double a = Math.PI * (Math.pow(r, 2) - Math.pow(r - t, 2));
		double e = 2.1e11;
		Constraint c1 = new Constraint(false, false, false);
		Constraint c2 = new Constraint(true, true, false);
		Force f1 = new Force(0, -4e3, -2e4);

		// create nodes
		Node n1 = struct.addNode(0.0, 0.0, lb * Math.sqrt(2.0 / 3.0));
		Node n2 = struct.addNode(0.0, lb / Math.sqrt(3), 0);
		Node n3 = struct.addNode(-lb / 2, -lb / Math.sqrt(12.0), 0);
		Node n4 = struct.addNode(lb / 2, -lb / Math.sqrt(12.0), 0);

		// apply BCs
		n1.setForce(f1);
		n2.setConstraint(c1);
		n3.setConstraint(c1);
		n4.setConstraint(c2);
		// n1.setPreLoadDisplacement(0, 10e-4, 10e-3);

		// create elements
		struct.addElement(e, a, 0, 1);
		struct.addElement(e, a, 0, 2);
		struct.addElement(e, a, 0, 3);
		struct.addElement(e, a, 1, 2);
		struct.addElement(e, a, 2, 3);
		struct.addElement(e, a, 3, 1);

		// struct.printStructure();
		// return the new structure
		return struct;
	}

	public static void main(String[] args) {
		Viewer viewer = new Viewer();
		Structure struct = createStructure();
		struct.solve();
		struct.printStructure();
		struct.printResults();
		Visualizer viz = new Visualizer(struct, viewer);

		// calculate radius scale
		double[] elementsRad = new double[struct.getNumberOfElements()];
		for (int i = 0; i < struct.getNumberOfElements(); i++) {
			elementsRad[i] = struct.getElement(i).getArea();
		}
		Arrays.sort(elementsRad);
		System.out.println("Biggest value is " + elementsRad[elementsRad.length - 1]);
		//double radius = Math.sqrt(elementsRad[elementsRad.length - 1] * 4 / 0.014);

		//viz.setRadiusScale(2);
		viz.drawElements();

		// viz.setConstraintScale(0.8);
		viz.drawConstraints();
		// viz.setArrowShaftScale(0.000025);
		// viz.setArrowRadiusScale(0.1);
		viz.drawForces();
		viz.setDisplacementScale(3e4);
		viz.drawDisplacements();
		// viz.setElementForceScale(1e-5);
		viz.drawElementForces();
		viewer.setVisible(true);

	}
}