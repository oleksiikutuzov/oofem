package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ParseFile {

	public static void main(String[] args) throws IOException {

		File file = new File("C:\\testFolder/SmallTetraeder.txt");
		Scanner scan = new Scanner(file);
		int line = 0;
		String lineText = "";
		
		do {
			lineText = scan.nextLine();
			line++;
		} while (lineText == "Nodes");
		
		System.out.println("Word 'Nodes' is on line " + line);
		
	}
}
