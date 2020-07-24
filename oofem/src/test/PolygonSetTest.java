package test;

import inf.v3d.obj.PolygonSet;
import inf.v3d.view.Viewer;

public class PolygonSetTest {

	public static void main(String[] args) {

		 Viewer v = new Viewer();
		 PolygonSet ps = new PolygonSet();
		 
		 ps.insertVertex(0, 0, 0, 0);
		 ps.insertVertex(1, 0, 0, 1);
		 ps.insertVertex(0, 1, 0, -1);
		 ps.polygonComplete();
		 
		 ps.insertVertex(1, 0, 0, 0);
		 ps.insertVertex(2, 0, 0, 0);
		 ps.insertVertex(2, 1, 0, 0);
		 ps.insertVertex(1, 1, 0, 0);
		 ps.polygonComplete();
		 
		 ps.setVisible(true);
		 ps.setColoringByData(true);
		 ps.setOutlinesVisible(true);
		 ps.setContourLinesVisible(true);
		 ps.createColors();
		 
		 v.addObject3D(ps);
		 v.setVisible(true);


	}

}
