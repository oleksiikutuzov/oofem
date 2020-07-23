package test;

import inf.v3d.obj.Arrow;
import inf.v3d.view.Viewer;

public class TestArrow {

	public static void main(String[] args) {
		Arrow arrow = new Arrow();
		
		arrow.setPoint1(0, 0, 0);
		arrow.setPoint2(1, 2, 0);
		
		Viewer v = new Viewer();
		v.addObject3D(arrow);
		
		v.setVisible(true);

	}

}
