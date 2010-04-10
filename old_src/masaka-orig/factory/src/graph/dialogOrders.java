package graph;

import files.ClientList;
import files.MainFrame;
import files.OrdersMade;
import files.TownOrder;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class dialogOrders extends JDialog implements ActionListener {

	private int screenHeight;
	private int screenWidth;
	private JPanel jpShow = new JPanel();
	private JTextField txtName;
	private String oldName, townName;
	private String vehicleName;
	private JLabel jlabel;
	private boolean clientOrder, editName = false;
	private ClientsBefore myGraph;
	private Dimension screen;
	private JButton btnSet;
	private ClientList clientLists;
	private MainFrame main;
	private Vector<Node> clientsAll;
	private Vector<Vector> clientsRoads;

	public dialogOrders(Dimension screen, ClientsBefore thisGraph, boolean orderMade, String titttle, JFrame OwnerForm, String txtClientName, String town) {
		super(OwnerForm, true);
		this.clientOrder = orderMade;
		this.myGraph = thisGraph;
		this.main = (MainFrame) OwnerForm;
		this.screen = screen;
		screenHeight = (screen.height / 4);
		screenWidth = (screen.width * 2 / 5);
		setTitle(titttle);

		jpShow.setLayout(null);

		btnSet = new JButton(titttle);
		btnSet.setBounds(70, 60, 170, 25);
		btnSet.addActionListener(this);

		jpShow.add(btnSet);

		jlabel = new JLabel(txtClientName);
		jlabel.setBounds(25, 20, 115, 25);
		jpShow.add(jlabel);

		this.townName = town;

		txtName = new JTextField();
		txtName.setHorizontalAlignment(JTextField.RIGHT);
		txtName.setBounds(130, 20, 150, 25);
		txtName.setText(vehicleName);
		txtName.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent ke) {
				char c = ke.getKeyChar();
				if (!((Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE)))) {
					getToolkit().beep();
					ke.consume();
				}
			}
		});

		jpShow.add(txtName);

		getContentPane().add(jpShow);
		setSize((screen.width * 1 / 3), (screen.height / 5));
		setResizable(false);
		setLocation((screen.width - 325) / 2, ((screen.height - 183) / 2));
		setVisible(true);

	}

	public dialogOrders(Dimension screen, ClientList list, boolean orderMade,boolean edit_names, String titttle, JFrame OwnerForm, String txtClientName) {
		super(OwnerForm, true);
		this.clientLists = list;
		this.clientOrder = orderMade;
		this.editName = edit_names;
		this.screen = screen;
		screenHeight = (screen.height / 4);
		screenWidth = (screen.width * 2 / 5);
		clientsAll = new Vector<Node>();
		clientsRoads = new Vector<Vector>();
		setTitle(titttle);
		this.oldName = txtClientName;
		jpShow.setLayout(null);

		btnSet = new JButton(titttle);
		btnSet.setBounds(70, 60, 170, 25);
		btnSet.addActionListener(this);
		jpShow.add(btnSet);

		try {
			InputStream is ; // = new InputStream("Clien.obj");

			is = ClientsBefore.class.getResource("Clien.obj").openStream();
			input_graphNeighbours(is);
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found.");
		} catch (IOException e) {
			System.err.println("Cannot access file.");
		}

		jlabel = new JLabel("Edit Name");
		jlabel.setBounds(25, 20, 115, 25);
		jpShow.add(jlabel);

		txtName = new JTextField();
		txtName.setHorizontalAlignment(JTextField.RIGHT);
		txtName.setBounds(115, 20, 150, 25);
		txtName.setText(txtClientName);

		jpShow.add(txtName);

		getContentPane().add(jpShow);
		setSize(screenWidth, screenHeight);
		setResizable(false);
		setLocation((screen.width - 325) / 2, ((screen.height - 183) / 2));
		setVisible(true);

	}

	public void actionPerformed(ActionEvent ae) {

		Object obj = ae.getSource();

		if (obj == btnSet) {

			if (txtName.getText().equals("")) {
				JOptionPane.showMessageDialog(null,"Please Fill in the required field first !!","Empty Record", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			if (this.clientOrder == true) {

				Vector<String> rowDataSingle = new Vector<String>();
				rowDataSingle.addElement(jlabel.getText());
				rowDataSingle.addElement(txtName.getText());
				rowDataSingle.addElement(this.townName);

				OrdersMade.reloadRecord(rowDataSingle);
				TownOrder.reloadRecord(rowDataSingle);
				dispose();
			}

			else if (this.clientOrder == false) {
				if (this.editName == true) {
					try {
						Properties props = new Properties();

						props.store(new FileOutputStream("graph/Clien.obj"),
								null);
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}

					try {
						FileWriter fileOpen = new FileWriter("graph/Clien.obj",
								true);
						PrintWriter cOut = new PrintWriter(new BufferedWriter(
								fileOpen));

						for (int z = 0; z < this.clientsAll.size(); z++) {
							if (this.clientsAll.get(z).name
									.contentEquals(this.oldName)) {
								cOut.println("'C'" + " '" + txtName.getText()
										+ "' " + this.clientsAll.get(z).x + " "
										+ this.clientsAll.get(z).y);
							} else {
								cOut.println("'C'" + " '"
										+ this.clientsAll.get(z).name + "' "
										+ this.clientsAll.get(z).x + " "
										+ this.clientsAll.get(z).y);

							}

						}

						for (int z = 0; z < this.clientsRoads.size(); z++) {
							if ((this.clientsRoads.get(z).get(0).toString())
									.contentEquals(this.oldName)) {
								cOut.println("'P'"
										+ " '"
										+ txtName.getText()
										+ "' '"
										+ this.clientsRoads.get(z).get(1)
												.toString()
										+ "' "
										+ this.clientsRoads.get(z).get(2)
												.toString());
							}

							if ((this.clientsRoads.get(z).get(1).toString())
									.contentEquals(this.oldName)) {
								cOut.println("'P'"
										+ " '"
										+ this.clientsRoads.get(z).get(0)
												.toString()
										+ "' '"
										+ txtName.getText()
										+ "' "
										+ this.clientsRoads.get(z).get(2)
												.toString());

							}

							if ((((this.clientsRoads.get(z).get(1).toString())
									.contentEquals(this.oldName)) == false)
									&& (((this.clientsRoads.get(z).get(0)
											.toString())
											.contentEquals(this.oldName)) == false)) {
								cOut.println("'P'"
										+ " '"
										+ this.clientsRoads.get(z).get(0)
												.toString()
										+ "' '"
										+ this.clientsRoads.get(z).get(1)
												.toString()
										+ "' "
										+ this.clientsRoads.get(z).get(2)
												.toString());

							}

						}

						cOut.close();

					} catch (IOException xp) {
						System.out.println(xp.getMessage());
					}

					this.clientLists.reloadRecord(false);
					JOptionPane.showMessageDialog(null," Name has been successfully Changed", "Set Name",JOptionPane.INFORMATION_MESSAGE);
					dispose();
				} else {
					int bestCarCapacity = Integer.MAX_VALUE;
					String bestCarNeeded = "";
					for (int i = 0; i < this.main.kim2.rowDataCollect.size(); i++) {
						if ((Integer.parseInt(this.main.kim2.rowDataCollect.get(i).get(1).toString()) <= bestCarCapacity)&& (Integer.parseInt(this.main.kim2.rowDataCollect.get(i).get(1).toString()) >= Integer.parseInt(txtName.getText()))) {
							bestCarCapacity = Integer.parseInt(this.main.kim2.rowDataCollect.get(i).get(1).toString());
							bestCarNeeded = this.main.kim2.rowDataCollect.get(i).get(0).toString();

						}
					}

					JOptionPane.showMessageDialog(null,"Its advisable to go with a " + bestCarNeeded,"Selection of car to take",JOptionPane.INFORMATION_MESSAGE);
					dispose();
					this.myGraph.paintRouteAfterUncertainities(this.myGraph.F,null, this.myGraph.nodeWhereIam);
				}
			}
		}

	}

	private void input_graphNeighbours(InputStream is) throws IOException {
		String s;

		Reader r = new BufferedReader(new InputStreamReader(is));
		StreamTokenizer st = new StreamTokenizer(r);
		st.commentChar('#');

		boolean b = true;

		try {

			while (b) {
				st.nextToken();
				s = st.sval;

				if (s.equals("C")) {
					Node client = new Node();
					st.nextToken();
					client.name = st.sval;
					st.nextToken();
					client.x = (int) st.nval;
					st.nextToken();
					client.y = (int) st.nval;

					this.clientsAll.addElement(client);
				} else if (s.equals("P")) {
					Vector road = new Vector();
					st.nextToken();
					road.add(st.sval);
					st.nextToken();
					road.add(st.sval);
					st.nextToken();
					road.add((int) st.nval);

					this.clientsRoads.addElement(road);
				}

			}

		} catch (NullPointerException x) {
		}

	}

}
