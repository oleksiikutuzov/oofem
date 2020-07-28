package test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import fem.CSVReader;
import fem.Structure;
import fem.Visualizer;
import inf.v3d.obj.Arrow;
import inf.v3d.view.Viewer;

public class GUI implements ActionListener {

	JFrame frame;
	JPanel panel;
	JPanel viewerPanel;
	JButton button;
	Visualizer viz;
	Structure struct;
	Viewer viewer;
	Object[][] obj;

	public GUI() {

		viewer = new Viewer();
		frame = new JFrame();
		button = new JButton("Apply");
		
		String modelPath = "C:\\testFolder/DomeTruss.csv";
		CSVReader reader = new CSVReader(modelPath);

		struct = reader.getValues();

		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 30));
		panel.setLayout(new GridLayout(1, 2));
		panel.setLocation(200, 300);
		button.setSize(20, 10);
		button.setBounds(540, 560, 80, 30);
		button.addActionListener(this);
		
		
		
//		viz.drawElements();
//		viz.drawConstraints();
//		viz.drawForces();
//		viz.drawDisplacements();
//		viz.drawElementForces();
		
		Container c = viewer.getContentPane();
		c.setBounds(10, 10, 500, 600);

		String[] columnNames = { "idx", "x1", "x2", "x3" };

		JTable table = new JTable(obj, columnNames);
		table.setBounds(540, 40, 300, 500);
		JTabbedPane tp = new JTabbedPane();
		JScrollPane js = new JScrollPane(table);
		js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		js.setVisible(false);
		tp.setBounds(540, 40, 300, 500);
		tp.add(js, "Nodes");
		frame.add(tp);
		frame.add(button);
//		frame.add(table);
		frame.add(c);
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Our GUI");
		frame.setSize(900, 660);
		frame.setResizable(false);
		frame.setVisible(true);
		
		this.struct.printStructure();
		viz = new Visualizer(this.struct, this.viewer);
		viz.transferScalesValues();
		viz.drawElements();
		viz.drawConstraints();
		viz.drawForces();
	

	}

	public static void main(String[] args) {
		new GUI();

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		

		
	}

}
