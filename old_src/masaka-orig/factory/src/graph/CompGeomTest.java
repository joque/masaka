package graph;

import files.ClientList;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CompGeomTest extends JDialog implements ActionListener{
	private JPanel canvasp;
    private JButton btnAdd;
	private ResultSet rs;
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	String imagename = "Images/MAPJPEG.jpg";
	ImageIcon ii = new ImageIcon(imagename);
	private String driver="sun.jdbc.odbc.JdbcOdbcDriver";
	String url="jdbc:odbc:moses";
	private JTextField txtName;
	private kim2 c;
	private cPointi th;
	private boolean thread = false;
	private JComboBox selectedroad;
	ComboBoxListener cbListener = new ComboBoxListener();
	private ClientList thisClientList;

	public boolean action(Event e, Object o) {
		if (e.target instanceof Checkbox) {

			c.repaint();
		}

		return true;
	}

	public boolean handleEvent(Event e) {
		if (e.id == Event.WINDOW_DESTROY) {
			if (thread)
				th.stop();// the thread is killed if it was alive
			hide();
			dispose();
			return true;
		}
		return super.handleEvent(e);
	}

	public CompGeomTest(boolean registerNew ,JFrame frame, ClientList thisList){  
		super(frame, true);
		setTitle("Register new Customer");
		this.thisClientList = thisList;
		canvasp = new JPanel();
		canvasp.setLayout(null);
    	JLabel townselect = new JLabel("Select the town from list");
    	townselect.setBounds (20,  20, 200, 25);
       	canvasp.add(townselect);
       	
    	JLabel closedroad = new JLabel("Enter Name of the customer");
    	closedroad.setBounds (200,  20, 200, 25);
       	canvasp.add(closedroad);
       	
    	JLabel closedroadselected = new JLabel("Town");
    	closedroadselected.setBounds (20,  50, 50, 25);
       	canvasp.add(closedroadselected);
       	
    	JLabel name = new JLabel("Name");
    	name.setBounds (190,  50, 50, 25);
       	canvasp.add(name);
       	
	    	selectedroad = new JComboBox();

			try {	
				Class.forName(driver);
		   		Connection connection=DriverManager.getConnection(url);
		   		Statement st = connection.createStatement();

					rs = st.executeQuery("select * from kla_towns");
					while (rs.next()) {
						selectedroad.addItem(rs.getString("NAME"));
					}
								
				connection.close();
			} catch (Exception ex) {System.out.println(ex);	}
			
			selectedroad.addItemListener(cbListener);	
			selectedroad.setBounds (60,  50, 100, 20);
			canvasp.add(selectedroad);

			txtName = new JTextField();
			txtName.setHorizontalAlignment(JTextField.RIGHT);
			txtName.setBounds(230, 50, 150, 20);
			canvasp.add(txtName);			   

			btnAdd = new JButton("Save");
			btnAdd.setBounds(170, 80, 100, 20);
			btnAdd.addActionListener(this);
			canvasp.add(btnAdd);
			
			setLayout(new BorderLayout());

			setSize((screen.width * 2/5), (screen.height * 2/9 ));
			setLocation((screen.width /3), (screen.height /3));
			getContentPane().add(canvasp, BorderLayout.CENTER);
			setVisible(true);
			
		}

	class ComboBoxListener implements ItemListener  {
  		public void itemStateChanged(ItemEvent e) {				
  			String sr = (String)e.getItem();

		/*	if (e.getSource().equals(jCombobox2))
			{
				String Sectionsel = jCombobox2.getSelectedItem().toString();
			}   */
		
	    }
	}

	public void actionPerformed (ActionEvent ae) {

	        Object obj = ae.getSource();
	        
	         if (obj == btnAdd){
					if (txtName.getText().equals("")) {
						JOptionPane.showMessageDialog(null,
								"Please Fill in the Client Name", "Empty Record",JOptionPane.INFORMATION_MESSAGE);
						return;
					} else {
						
						try {		
							Class.forName(driver);
					   		Connection connection=DriverManager.getConnection(url);
					   		Statement st = connection.createStatement();
					   		String s ="INSERT INTO clientList (name,town) VALUES ('"
										+ txtName.getText()
										+ "' , '"
										+ selectedroad.getSelectedItem().toString() + "')";
					   		st.executeUpdate(s);
					   		
					   		connection.close();
						} catch (Exception sqlE) {
							JOptionPane.showMessageDialog(null, "Record already exists...","Record Error", JOptionPane.INFORMATION_MESSAGE);
						}
						
						this.thisClientList.reloadRecord(false);
						dispose();

					}

				}
					
	   }

}
