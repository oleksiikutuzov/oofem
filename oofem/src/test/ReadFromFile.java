package test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class ReadFromFile {

	private static double[] getDoubleArray(String strLine) {
		double[] a;
		String[] split = strLine.split("[,)]"); // split the line at the ',' and ')' characters
		a = new double[split.length - 1];
		for (int i = 0; i < a.length; i++) {
			a[i] = Double.parseDouble(split[i + 1]); // get the double value of the String
		}
		return a;
	}

	public void extractNodes() {

		try {
			FileInputStream fstream = new FileInputStream("input.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;
			int lineNumber = 0;
			double[] a = null;
			double[] b = null;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				lineNumber++;
				if (lineNumber == 4) {
					a = getDoubleArray(strLine);
				} else if (lineNumber == 5) {
					b = getDoubleArray(strLine);
				}
			}
			// Close the input stream
			fstream.close();
			// print the contents of a
			for (int i = 0; i < a.length; i++) {
				System.out.println("a[" + i + "] = " + a[i]);
			}
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

}
