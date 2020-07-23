package test;

import fem.Structure;
import models.SmallTetraeder;

public class EnumerateTest2 {
	public static void main(String[] args) {
		Structure struct = SmallTetraeder.createStructure();
		// solve
		struct.solve();

	}
}