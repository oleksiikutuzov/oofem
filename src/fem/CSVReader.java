package fem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader {

	private Structure struct;
	private String path;
	private File file;
	private static String line = "";
	private boolean addNodes = false;
	private boolean addConstraints = false;
	private boolean addForces = false;
	private boolean addElements = false;
	private boolean addVisual = false;
	BufferedReader br;

	public CSVReader(String path) {
		this.path = path;
	}
	
	public CSVReader(File file) {
		this.file = file;
	}
	
	// set path for file with model
	public void setPath(String s) {
		this.path = s;
	}

	// get values from file and return Structure object
	public Structure getValues() {
		try {
			// initialize Structure object
			this.struct = new Structure();
			// initialize BufferedReader
			if (file == null) {
			br = new BufferedReader(new FileReader(path));
			} else {
			br = new BufferedReader(new FileReader(file));
			}

			// BufferedReader going through all lines until end of file
			while ((line = br.readLine()) != null) {

				// skip lines with headers of tables
				if (line.contains("id") || line.contains("node") || line.contains("Radius")) {
					// skip this line
					continue;
				}

				// search for Nodes table
				if (line.contains("Node")) {
					// set flag to add Node values
					addNodes = true;
					// skip next line
					continue;
				}

				// get node values
				if (addNodes == true) {
					// split line by commas
					String[] nodeValues = line.split(",");
					// check if line has no elements
					if (line.contains(",,,")) {
						// remove flag
						addNodes = false;
						// skip next line
						continue;
					}
					// add Node with values from table
					this.struct.addNode(Double.parseDouble(nodeValues[1]), Double.parseDouble(nodeValues[2]),
							Double.parseDouble(nodeValues[3]));
				}

				// search for Constraints table
				if (line.contains("Constraints")) {
					// set flag to add Constraints values
					addConstraints = true;
					// skip next line
					continue;
				}

				// get constraint values
				if (addConstraints == true) {
					// split line by commas
					String[] constraintValues = line.split(",");
					// check if line has no elements
					if (line.contains(",,,")) {
						// remove flag
						addConstraints = false;
						// skip next line
						continue;
					}

					// create an array of boolean values with constraints values
					boolean[] c = new boolean[3];
					for (int i = 1; i < 4; i++) {
						if (constraintValues[i].contains("fixed")) {
							c[i - 1] = false;
						} else if (constraintValues[i].contains("free")) {
							c[i - 1] = true;
						}
					}
					// set Constraint to Node
					this.struct.getNode(Integer.parseInt(constraintValues[0]))
							.setConstraint(new Constraint(c[0], c[1], c[2]));
				}

				// search for Forces table
				if (line.contains("Forces")) {
					// set flag to add Forces values
					addForces = true;
					// skip next line
					continue;
				}

				// get force values
				if (addForces == true) {
					// split line by commas
					String[] forceValues = line.split(",");
					// check if line has no elements
					if (line.contains(",,,")) {
						// remove flag
						addForces = false;
						// skip next line
						continue;
					}
					// create an array with forces values
					double[] c = new double[3];
					for (int i = 0; i < 3; i++) {
						c[i] = Double.parseDouble(forceValues[i + 1]);
					}
					// set Force values to Node
					this.struct.getNode(Integer.parseInt(forceValues[0])).setForce(new Force(c[0], c[1], c[2]));
				}

				// search for Elements table
				if (line.contains("Elements")) {
					// set flag to add Elements values
					addElements = true;
					// skip next line
					continue;
				}

				// get Elements values
				if (addElements == true) {
					// split line by commas
					String[] elementValues = line.split(",");
					// check if line has no elements
					if (line.contains(",,,")) {
						// remove flag
						addElements = false;
						// skip next line
						continue;
					}
					// add an Element to the Structure
					this.struct.addElement(Double.parseDouble(elementValues[1]), Double.parseDouble(elementValues[2]),
							Integer.parseInt(elementValues[3]), Integer.parseInt(elementValues[4]));
				}

				// search for Visual table
				if (line.contains("Visual")) {
					// set flag to add Forces values
					addVisual = true;
					// skip next line
					continue;
				}

				// get scales for Visualizer
				if (addVisual == true) {
					// split line by commas
					String[] visualValues = line.split(",");
					// check if line has no elements
					if (line.contains(",,,")) {
						// remove flag
						addVisual = false;
						// skip next line
						continue;
					}
					// create an array with Scales values
					double[] viewerScales = new double[6];
					for (int i = 0; i < viewerScales.length; i++) {
						viewerScales[i] = Double.parseDouble(visualValues[i]);
					}
					// set Scales values to the Structure
					this.struct.setViewerScales(viewerScales);
				}
			}
			// close BufferedReader
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.struct;
	}
}
