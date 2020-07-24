package fem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader {

	private Structure struct;
	private String path;
	private static String line = "";
	private boolean addNodes = false;
	private boolean addConstraints = false;
	private boolean addForces = false;
	private boolean addElements = false;

	public void setPath(String s) {
		this.path = s;
	}

	public Structure getValues() {
		try {

			this.struct = new Structure();
			BufferedReader br = new BufferedReader(new FileReader(path));

			while ((line = br.readLine()) != null) {

				//System.out.println(line);

				if (line.contains("id") || line.contains("node")) {
					continue;
				}

				// search for Nodes part
				if (line.contains("Node")) {
					addNodes = true;
					continue;
				}

				if (addNodes == true)	{

					// get node values
					String[] nodeValues = line.split(",");
					if (line.contains(",,,")) {
						addNodes = false;
						continue;
					}

					// System.out.println(ArrayFormat.format(values));
					this.struct.addNode(Double.parseDouble(nodeValues[1]), Double.parseDouble(nodeValues[2]),
							Double.parseDouble(nodeValues[3]));
					//System.out.println("Node " + nodeValues[0] + " position is set to   " + ArrayFormat
					//		.format(this.struct.getNode(Integer.parseInt(nodeValues[0])).getPosition().toArray()));
					// System.out.print(
					// ArrayFormat.format(struct.getNode(Integer.parseInt(values[0])).getPosition().toArray())
					// + "\n");
					}

				// search for Constraints part
				if (line.contains("Constraints")) {
					addConstraints = true;
					continue;
				}

				if (addConstraints == true) {
					// get constraint values
					String[] constraintValues = line.split(",");
					if (line.contains(",,,")) {
						addConstraints = false;
						continue;
					}
					//System.out.println(ArrayFormat.format(constraintValues));
					boolean[] c = new boolean[3];
					for (int i = 1; i < 4; i++) {
						if (constraintValues[i].contains("fixed")) {
							c[i - 1] = false;
						} else if (constraintValues[i].contains("free")) {
							c[i - 1] = true;
						}
					}
					//System.out.println("c vector: " + ArrayFormat.format(c));
					this.struct.getNode(Integer.parseInt(constraintValues[0]))
							.setConstraint(new Constraint(c[0], c[1], c[2]));
//					System.out.println(
//							"Node " + constraintValues[0] + " constraint is set to  " + ArrayFormat.format(this.struct
//									.getNode(Integer.parseInt(constraintValues[0])).getConstraint().getStringArray()));
				}
				
				// search for Constraints part
				if (line.contains("Forces")) {
					addForces = true;
					continue;
				}

				if (addForces == true) {
					// get force values
					String[] forceValues = line.split(",");
					if (line.contains(",,,")) {
						addForces = false;
						continue;
					}
					//System.out.println(ArrayFormat.format(constraintValues));
					double[] c = new double[3];
					for (int i = 0; i < 3; i++) {
						c[i] =  Double.parseDouble(forceValues[i+1]);
					}
					//System.out.println("c vector: " + ArrayFormat.format(c));
					this.struct.getNode(Integer.parseInt(forceValues[0])).setForce(new Force(c[0], c[1], c[2]));
//					System.out.println(
//							"Node " + forceValues[0] + " force is set to      " + ArrayFormat.format(this.struct
//									.getNode(Integer.parseInt(forceValues[0])).getForce().getComponentArray()));
				}
				
				// search for Elements part
				if (line.contains("Elements")) {
					addElements = true;
					continue;
				}

				if (addElements == true) {
					// get force values
					String[] elementValues = line.split(",");
					if (line.contains(",,,")) {
						addElements = false;
						continue;
					}
					this.struct.addElement(Double.parseDouble(elementValues[1]), Double.parseDouble(elementValues[2]), Integer.parseInt(elementValues[3]), Integer.parseInt(elementValues[4]));
//					System.out.println(
//							"Element " + elementValues[0] + " force is set for nodes " + elementValues[3] + " and " + elementValues[4]);
//					System.out.println("Element " + elementValues[0] + ": E = " + elementValues[1] + ", A = " + elementValues[2]);
				}

			}
			br.close();
		}

		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.struct;
	}
}
