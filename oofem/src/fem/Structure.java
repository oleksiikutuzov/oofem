package fem;

import iceb.jnumerics.*;
import java.util.*;

public class Structure {
	
	private ArrayList<Node> nodes = new ArrayList<Node>();
	private ArrayList<Element> elements = new ArrayList<Element>();
	
	public void addNode(Node n) {
		this.nodes.add(n);
	}
	
	public void addElement(Element e) {
		this.elements.add(e);
	}
	
	public int getNumberOfNodes() {
		return this.nodes.size();
	}

	public Node getNode(int id) {
		return this.nodes.get(id);
	}
	
	public int getNumberOfElements() {
		return this.elements.size();
	}
	
	public Element getElement(int id) {
		return this.elements.get(id);
	}
	
}
