package models;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import fem.CSVReader;
import fem.Structure;
import fem.Visualizer;
import inf.text.ArrayFormat;
import inf.v3d.view.Viewer;

public class StructureFromFile {

	public static void main(String[] args) throws IOException {
		
		Viewer viewer = new Viewer();
		CSVReader reader = new CSVReader();
		
		// define path to the model
		String modelPath = "C:\\testFolder/test/test2/Tetraeder.csv";
		
		reader.setPath(modelPath);
		Structure struct = reader.getValues();
		String[] part1 = modelPath.split("/");
		String[] part2 = part1[part1.length-1].split("\\.");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		String outputPath = "";
		for (int i = 0; i < part1.length-1; i++) {
			outputPath = outputPath.concat(part1[i]);
			outputPath = outputPath.concat("/");
		}
		struct.setWritePath(outputPath + part2[0] + "_solution_" + df.format(date) + ".txt");
		
		struct.solve();
//		struct.printStructure();
//		struct.printResults();
		struct.writeToFile();
		
		Visualizer viz = new Visualizer(struct, viewer);
		viz.setRadiusScale(1000);
		viz.drawElements();
		viz.setConstraintScale(5e2);
		viz.drawConstraints();
		viz.setArrowShaftScale(1);
		viz.setArrowRadiusScale(100);
		viz.drawForces();
		viz.setDisplacementScale(2e-14);
//		viz.drawDisplacements();
		viz.setElementForceScale(1e-6); 
//		viz.drawElementForces();
		viewer.setVisible(false);
		
	}

}
