package models;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import fem.CSVReader;
import fem.Structure;
import fem.Visualizer;
import inf.v3d.view.Viewer;

public class StructureFromFile {

	public static void main(String[] args) throws IOException {
		
		Viewer viewer = new Viewer();
		CSVReader reader = new CSVReader();
		reader.setPath("C:\\testFolder/SmallTetraeder.csv");
		Structure struct = reader.getValues();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		System.out.println(df.format(date));
		struct.setWritePath("C:\\testFolder/SmallTetraeder_solution_" + df.format(date) + ".txt");
		struct.solve();
		struct.printStructure();
		struct.printResults();
		//struct.writeToFile();
		
		Visualizer viz = new Visualizer(struct, viewer);
		viz.drawElements();
		viz.setConstraintSymbolScale(0.8);
		viz.drawConstraints();
		viz.setForceSymbolScale(0.000025);
		viz.setForceSymbolRadius(0.1);
		viz.drawForces();
		viz.setDisplacementScale(3e3);
		viz.drawDisplacements();
		viz.setForceScale(1e-7); 
//		viz.drawElementForces();
		viewer.setVisible(true);
		
	}

}
