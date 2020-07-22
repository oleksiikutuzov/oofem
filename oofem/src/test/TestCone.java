package test;

import inf.v3d.obj.Cone;
import inf.v3d.view.Viewer;

public class TestCone {
	
	public static void main(String[] args) {
		
		Cone c = new Cone();
		c.setCenter(0.0, 0.0, 0.0);
		
		Viewer v = new Viewer();
		v.addObject3D(c);
		
		v.setVisible(true);
	}

}
