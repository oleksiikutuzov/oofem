package test;

import iceb.jnumerics.Vector3D;
import inf.text.ArrayFormat;
import inf.v3d.obj.CylinderSet;
import inf.v3d.obj.PolygonSet;
import inf.v3d.view.Viewer;

public class PolygonSetTest {

	public static void main(String[] args) {

		 Viewer v = new Viewer();
		 PolygonSet ps = new PolygonSet();
		 CylinderSet cs = new CylinderSet();
		 
		 double[] node1_pos = new double[] {0,0,1.224745E01};
		 double[] node2_pos = new double[] {0,8.660254E00,0};
		 cs.addCylinder(node1_pos, node2_pos, 0.1);
		 
		 Vector3D node1 = new Vector3D(node1_pos);
		 Vector3D node2 = new Vector3D(node2_pos);
		 
		 Vector3D d_num = node2.subtract(node1);
		 double d_den = Math.pow(d_num.normTwo(), -1);
		 Vector3D d = d_num.multiply(d_den);
		 
		 Vector3D p1 = d.vectorProduct(node2);
		 Vector3D p2 = d.vectorProduct(node1);
		 
		 
		 
		 System.out.println("node1 " + ArrayFormat.format(node1.toArray()));
		 System.out.println("node2 " + ArrayFormat.format(node2.toArray()));
		 System.out.println("node1_norm " + ArrayFormat.format(d_num.toArray()));
		 System.out.println("node2_norm " + ArrayFormat.format(d.toArray()));
		 System.out.println("p1 " + ArrayFormat.format(p1.toArray()));
		 System.out.println("p2 " + ArrayFormat.format(p2.toArray()));
		 
		 
		
		 
		 ps.insertVertex(node1_pos, 1);
		 ps.insertVertex(node2_pos, 1);
		 ps.insertVertex(node2.add(p1.multiply(0.4)).toArray(), 1);
		 ps.insertVertex(node1.add(p1.multiply(0.4)).toArray(), 1);
		
		 ps.polygonComplete();

		 
		 
		 v.addObject3D(cs);
		 v.addObject3D(ps);
		 v.setVisible(true);


	}

}
