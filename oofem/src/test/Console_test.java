package test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import fem.CSVReader;
import fem.Structure;
import fem.Visualizer;
import iceb.jnumerics.MatrixFormat;
import inf.text.ArrayFormat;
import inf.v3d.view.Viewer;

public class Console_test {

	public static void main(String[] args) {
		new Console_test();

	}

	public JFrame frame;
	public JTextPane console;
	public JTextField input;
	public JScrollPane scrollpane;

	public StyledDocument document;

	boolean trace = false;

	Color warn = new Color(255, 50, 50);

	private Structure struct;
	private Viewer viewer;
	private CSVReader reader;
	private Visualizer viz;
	private File file;
	private JFileChooser chooser;
	private JFileChooser saver;

	ArrayList<String> recentUsed = new ArrayList<String>();
	int recentUsedId = 0;
	int recentUsedMax = 10;

	public Console_test() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
		}

		viewer = new Viewer();
		viewer.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		frame = new JFrame();
		frame.setTitle("Console");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				input.requestFocus();
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}
		});

		console = new JTextPane();
		console.setEditable(false);
		console.setFont(new Font("Courier New", Font.PLAIN, 12));

		document = console.getStyledDocument();

		input = new JTextField();
		input.setEditable(true);
		input.setFont(new Font("Courier New", Font.PLAIN, 12));
	

		input.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String text = input.getText();

				if (text.length() > 1) {

					recentUsed.add(text);
					recentUsedId = 0;

					println(text, false);
					doCommand(text);
					scrollBottom();
					input.selectAll();
				}

			}

		});

		input.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					if (recentUsedId < (recentUsedMax - 1) && recentUsedId < (recentUsed.size() - 1)) {
						recentUsedId++;
					}

					input.setText(recentUsed.get(recentUsed.size() - 1 - recentUsedId));
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					if (recentUsedId > 0) {
						recentUsedId--;
					}
					input.setText(recentUsed.get(recentUsed.size() - 1 - recentUsedId));
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

		});

		scrollpane = new JScrollPane(console);
		scrollpane.setBorder(null);
	
		Component pan = viewer.getContentPane();
		
		JPanel p = new JPanel();
		p.setSize(new Dimension(300, 400));
//		p.add(input, BorderLayout.SOUTH);
//		p.add(scrollpane, BorderLayout.CENTER);

		frame.add(input, BorderLayout.SOUTH);
		frame.add(scrollpane, BorderLayout.CENTER);
		
		
		
//		frame.add(p, BorderLayout.CENTER);
//		frame.add(pan, BorderLayout.WEST);
		
		
		frame.setSize(800, 500);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	public void print(String s, boolean trace) {
		print(s, trace, new Color(0, 0, 0));
	}

	public void print(String s, boolean trace, Color c) {
		Style style = console.addStyle("Style", null);
		StyleConstants.setForeground(style, c);

		if (trace) {
			Throwable t = new Throwable();
			StackTraceElement[] elements = t.getStackTrace();
			String caller = elements[0].getClassName();

			s = caller + " -> " + s;
		}

		try {
			document.insertString(document.getLength(), s, style);
		} catch (Exception ex) {
		}
	}

	public void println(String s, boolean trace) {
		print(s + "\n", trace, new Color(0, 0, 0));
	}

	public void println(String s, boolean trace, Color c) {
		print(s + "\n", trace, c);
	}

	public void doCommand(String s) {
		final String[] commands = s.split(" ");

		try {
			if (commands[0].equalsIgnoreCase("clear")) {
				clear();
			} else if (commands[0].equalsIgnoreCase("popup")) {
				String message = "";
				for (int i = 1; i < commands.length; i++) {
					message += commands[i];
					if (i != commands.length - 1) {
						message += " ";
					}
					JOptionPane.showMessageDialog(null, message, "Message", JOptionPane.INFORMATION_MESSAGE);

				}
			} else if (commands[0].equalsIgnoreCase("structure")) {

				if (commands[1].equalsIgnoreCase("new")) {
					struct = new Structure();
				} else if (commands[1].equalsIgnoreCase("importByPath")) {

					struct = new Structure();
					String modelPath = commands[2];

					reader = new CSVReader(modelPath);
					struct = reader.getValues();

					println("Structure import done!", false);

				} else if (commands[1].equalsIgnoreCase("draw")) {

					if (struct == null) {
						println("You need to create structure first", false, warn);
					} else {
						if (viewer.isVisible()) {
							viewer = new Viewer();
							viewer.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
							viz = new Visualizer(struct, viewer);
							viz.transferScalesValues();
							viz.setNodeScale(2e-1);
							viz.drawElements();
							viz.drawConstraints();
							viz.drawForces();
							viz.drawNodes();
							viewer.setVisible(true);
						} else {
							viewer = new Viewer();
							viewer.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
							viz = new Visualizer(struct, viewer);
							viz.transferScalesValues();
							viz.setNodeScale(2e-1);
							viz.drawElements();
							viz.drawConstraints();
							viz.drawForces();
							viz.drawNodes();
							viewer.setVisible(true);
						}
					}

				} else if (commands[1].equalsIgnoreCase("print")) {
					printStructure();

				} else if (commands[1].equalsIgnoreCase("import")) {
					JFileChooser fc = getFileChooser();
					int returnVal = fc.showOpenDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						file = fc.getSelectedFile();
					}
					struct = new Structure();
					CSVReader reader = new CSVReader(file);
					struct = reader.getValues();

					println("Structure import done!", false);
				} else if (commands[1].equalsIgnoreCase("add")) {

					if (this.struct == null) {
						println("You need to create structure first", false, warn);
					} else if (commands.length <= 2) {
						println("Please provide input", false, warn);
					} else {

						if (commands[2].equalsIgnoreCase("node")) {
							try {
								struct.addNode(Double.parseDouble(commands[3]), Double.parseDouble(commands[4]),
										Double.parseDouble(commands[5]));
							} catch (NumberFormatException e) {
								print("Please enter numbers", false, warn);
							} catch (NullPointerException n) {
								print("Your input is not full", false, warn);
							}
						} else if (commands[2].equalsIgnoreCase("element")) {
							try {
								struct.addElement(Double.parseDouble(commands[3]), Double.parseDouble(commands[4]),
										Integer.parseInt(commands[5]), Integer.parseInt(commands[6]));
							} catch (NumberFormatException e) {
								print("Please enter numbers", false, warn);
							} catch (NullPointerException e) {
								print("Your input is not full", false, warn);
							} catch (IndexOutOfBoundsException e) {
								print("One of the nodes does not exist", false, warn);
							}
						} else {
							println("Wrong input", false, warn);
						}
					}

				} else if (commands[1].equalsIgnoreCase("modify")) {

					if (this.struct == null) {
						println("You need to create structure first", false, warn);
					} else if (commands.length <= 2) {
						println("Please provide input", false, warn);
					} else {

						if (commands[2].equalsIgnoreCase("node")) {
							try {
								struct.editNode(Integer.parseInt(commands[3]), Double.parseDouble(commands[4]),
										Double.parseDouble(commands[5]), Double.parseDouble(commands[6]));
							} catch (NumberFormatException e) {
								print("Please enter numbers", false, warn);
							} catch (NullPointerException n) {
								print("Your input is not full", false, warn);
							} catch (IndexOutOfBoundsException e) {
								print("One of the values does not exist or input isn't full", false, warn);
							}
						} else if (commands[2].equalsIgnoreCase("element")) {
							try {
								struct.editElement(Integer.parseInt(commands[3]), Double.parseDouble(commands[4]),
										Double.parseDouble(commands[5]), Integer.parseInt(commands[6]),
										Integer.parseInt(commands[7]));
							} catch (NumberFormatException e) {
								print("Please enter numbers", false, warn);
							} catch (NullPointerException e) {
								print("Your input is not full", false, warn);
							} catch (IndexOutOfBoundsException e) {
								print("One of the nodes does not exist or input isn't full", false, warn);
							}
						} else {
							println("Wrong input", false, warn);
						}
					}

				} else if (commands[1].equalsIgnoreCase("solve")) {
					if (this.struct == null) {
						println("You need to create structure first", false, warn);
					} else {
						long start = System.currentTimeMillis();
						struct.solve(true);
						long elapsedTimeMillis = System.currentTimeMillis() - start;
						println("Calculation done in " + elapsedTimeMillis + " ms\n", false);
					}
				} else {
					println("Wrong command 2!", trace, new Color(250, 50, 50));
				}

			} else if (commands[0].equalsIgnoreCase("result")) {
				if (commands[1].equalsIgnoreCase("draw")) {
					if (viewer.isVisible()) {
						viz.drawDisplacements();
						viz.drawElementForces();
						viewer.setVisible(true);
					} else {
						viewer = new Viewer();
						viewer.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
						viz = new Visualizer(struct, viewer);
						viz.transferScalesValues();
						viz.setNodeScale(2e-1);
						viz.drawElements();
						viz.drawConstraints();
						viz.drawForces();
						viz.drawNodes();
						viz.drawDisplacements();
						viz.drawElementForces();
						viewer.setVisible(true);
					}
				} else if (commands[1].equalsIgnoreCase("export")) {
					// add current date and time
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
					Date date = new Date();
					String exportPath = new String(file.getPath().substring(0, file.getPath().length() - 4)
							+ "_solution_" + df.format(date) + ".txt");
					saver = getFileSaver(file.getParent());
					saver.setSelectedFile(new File(exportPath));
					int returnVal = saver.showSaveDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						String filename = saver.getSelectedFile().getPath().substring(0,
								saver.getSelectedFile().getPath().length() - 4);
						if (!filename.endsWith(".txt")) {
							filename = filename + ".txt";
							struct.writeToFile(filename);
							println("Write to file done " + filename, false);
						} else {
							println("Wrong command 2!", trace, new Color(250, 50, 50));
						}
					}
				} else if (commands[1].equalsIgnoreCase("print")) {
					printResult();
				}
			}
		} catch (Exception ex) {
			println("Error -> " + ex.getClass() + ": " + ex.getMessage(), trace, new Color(250, 50, 50));
		}

	}

	public void scrollTop() {
		console.setCaretPosition(0);
	}

	public void scrollBottom() {
		console.setCaretPosition(console.getDocument().getLength());
	}

	public void clear() {
		try {
			document.remove(0, document.getLength());
		} catch (Exception ex) {
		}
	}

	public void printStructure() {
		println("Listing structure\n", false);
		println("Nodes", false);
		println(ArrayFormat.iFormat("  idx            x1             x2             x3"), false);
		for (int i = 0; i < this.struct.getNumberOfNodes(); i++) {
			println(ArrayFormat.format(i) + MatrixFormat.format(this.struct.getNode(i).getPosition()), false);
		}

		println("\nConstraints", false);
		println(ArrayFormat.iFormat(" node            u1             u2             u3"), false);
		for (int i = 0; i < this.struct.getNumberOfNodes(); i++) {
			if (this.struct.getNode(i).getConstraint() != null) {
				println(ArrayFormat.format(i)
						+ ArrayFormat.format(this.struct.getNode(i).getConstraint().getStringArray()), false);
			}
		}

		println("\nForces", false);
		println(ArrayFormat.iFormat(" node            r1             r2             r3"), false);
		for (int i = 0; i < this.struct.getNumberOfNodes(); i++) {
			if (this.struct.getNode(i).getForce() != null) {
				println(ArrayFormat.format(i)
						+ ArrayFormat.format(this.struct.getNode(i).getForce().getComponentArray()), false);
			}
		}

		println("\nElements", false);
		println(ArrayFormat.iFormat("  idx             E              A         length"), false);
		for (int i = 0; i < this.struct.getNumberOfElements(); i++) {
			println(ArrayFormat.format(i) + ArrayFormat.format(this.struct.getElement(i).getEModulus())
					+ ArrayFormat.format(this.struct.getElement(i).getArea())
					+ ArrayFormat.format(this.struct.getElement(i).getLength()), false);
		}

	}

	public void printResult() {
		println("\nListing analysis results", false);
		println("\nDisplacements", false);
		println(ArrayFormat.iFormat(" node            u1             u2             u3"), false);
		for (int i = 0; i < this.struct.getNumberOfNodes(); i++) {
			if (this.struct.getNode(i).getDisplacement() != null) {
				println(ArrayFormat.format(i) + MatrixFormat.format(this.struct.getNode(i).getDisplacement()), false);
			}
		}
		println("\nElement forces", false);
		println(ArrayFormat.iFormat(" elem         force"), false);
		for (int i = 0; i < this.struct.getNumberOfElements(); i++) {
			println(ArrayFormat.format(i) + ArrayFormat.format(this.struct.getElement(i).computeForce()), false);
		}
	}

	protected JFileChooser getFileChooser() {
		if (chooser == null) {
			chooser = new JFileChooser();
			FileFilter csv = new FileNameExtensionFilter("CSV file(*.csv)", "csv");
			chooser.addChoosableFileFilter(csv);
			chooser.setFileFilter(csv);
			chooser.setAcceptAllFileFilterUsed(false);
		}
		return chooser;
	}

	protected JFileChooser getFileSaver(String path) {
		if (saver == null) {
			saver = new JFileChooser(path);
			FileFilter txt = new FileNameExtensionFilter("Text file(*.txt)", "txt");
			saver.addChoosableFileFilter(txt);
			saver.setFileFilter(txt);
			saver.setAcceptAllFileFilterUsed(false);
		}
		return saver;
	}

}
