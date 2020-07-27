package fem;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import inf.v3d.view.Viewer;

public class StructureFromFile {

	public static void main(String[] args) throws IOException {

		// initialize Viewer and reader
		Viewer viewer = new Viewer();
		CSVReader reader = new CSVReader();

		// define path to the model
		String modelPath = "C:\\testFolder/DomeTruss.csv";

		// set path to the model
		reader.setPath(modelPath);
		// create Structure by getting values from file
		Structure struct = reader.getValues();
		// initialize Visualizer
		Visualizer viz = new Visualizer(struct, viewer);
		// set Scales to values from file
		viz.transferScalesValues();

		// set path to output file to the same folder
		String[] part1 = modelPath.split("/");
		String[] part2 = part1[part1.length - 1].split("\\.");
		// add current date and time
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		String outputPath = "";
		for (int i = 0; i < part1.length - 1; i++) {
			outputPath = outputPath.concat(part1[i]);
			outputPath = outputPath.concat("/");
		}
		struct.setWritePath(outputPath + part2[0] + "_solution_" + df.format(date) + ".txt");

		// count time of calculation
		long start = System.currentTimeMillis();
		struct.solve();
		long elapsedTimeMillis = System.currentTimeMillis() - start;
		System.out.println("Calculation done in " + elapsedTimeMillis + " ms\n");

		// print structure and result to console
//		struct.printStructure();
//		struct.printResults();

		// print structure and result to file
//		struct.writeToFile();

		// overwrite scale values
//		viz.setRadiusScale(2);
//		viz.setConstraintScale(0.4);
//		viz.setArrowShaftScale(4e-5);
//		viz.setArrowRadiusScale(7e-2);
//		viz.setDisplacementScale(1e2);
//		viz.setElementForceScale(1e-6); 

		// draw elements and solution
		viz.drawElements();
		viz.drawConstraints();
		viz.drawForces();
		viz.drawDisplacements();
		viz.drawElementForces();
		viewer.setVisible(true);
	}
}
