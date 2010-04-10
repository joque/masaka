package files;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class dialogVehicle extends JDialog implements ActionListener {

	private JPanel jpShow = new JPanel();
	private JButton btnSet;
	private JTextField txtName, txtCapacity;
	private int screenHeight;
	private int screenWidth;
	private String sqlCondition;
	private static final String driver="sun.jdbc.odbc.JdbcOdbcDriver";
	String url="jdbc:odbc:moses";
	private VehiclesAvailable thisVehiclesAvailable;

	dialogVehicle(VehiclesAvailable thisOne, String titttle, JFrame OwnerForm, String vehicleName, String vehicleCapacity, String sqlCondition, Dimension screen) {
		super(OwnerForm, true);
		setTitle(titttle);
		this.thisVehiclesAvailable = thisOne;
		this.sqlCondition = sqlCondition;
    	screenHeight = (screen.height/4);
    	screenWidth = (screen.width * 2 / 5);

    	jpShow.setLayout(null);

		btnSet = new JButton("Save");
		btnSet.setBounds(screenWidth*4/10, screenHeight*5/10, screenWidth*2/10, 25);
		btnSet.addActionListener(this);

		jpShow.add(btnSet);
		// nowForm = currentForm;

		JLabel jlabel = new JLabel("Vehicle Name :");
		jlabel.setBounds(screenWidth/10, screenHeight/10,  screenWidth*3/10, 25);
		jpShow.add(jlabel);

		txtName = new JTextField();
		txtName.setHorizontalAlignment(JTextField.RIGHT);
		txtName.setBounds(screenWidth*8/20, screenHeight*1/10, screenWidth*8/20, 25);
		txtName.setText(vehicleName);

		if (sqlCondition.contentEquals("Edit")) {
			txtName.disable();
		}
		jpShow.add(txtName);

		JLabel jlabel4 = new JLabel("Vehicle Capacity :");
		jlabel4.setBounds(screenWidth/10, screenHeight*3/10, screenWidth*3/10, 25);
		jpShow.add(jlabel4);

		txtCapacity = new JTextField();
		txtCapacity.setHorizontalAlignment(JTextField.RIGHT);
		txtCapacity.setBounds(screenWidth*8/20, screenHeight*3/10, screenWidth*8/20, 25);
		txtCapacity.setText(vehicleCapacity);
		txtCapacity.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent ke) {
				char c = ke.getKeyChar();
				if (!((Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE)))) {
					getToolkit().beep();
					ke.consume();
				}
			}
		});
		jpShow.add(txtCapacity);

		getContentPane().add(jpShow);
		setSize(screenWidth, screenHeight);
		setResizable(false);
		setLocation((screen.width - 325) / 2, ((screen.height - 283) / 2));
		setVisible(true);

	}

	public void actionPerformed(ActionEvent ae) {

		Object obj = ae.getSource();

		if (obj == btnSet) {

				if (txtName.getText().equals("")) {
					JOptionPane.showMessageDialog(null,"Please Fill in the Vehicle Name", "Empty Record",JOptionPane.INFORMATION_MESSAGE);
					return;
				} else {

					if (sqlCondition == "New") {
						  try {			  					
								Class.forName(driver);					
								Connection connection=DriverManager.getConnection(url);
						   		Statement statement = connection.createStatement();
				
						   		String s ="INSERT INTO Vehicles (VehicleName,Capacity) VALUES ('"
									+ txtName.getText()
									+ "',"
									+ txtCapacity.getText() + ")";
						   		statement.executeUpdate(s);
						   		connection.close();
						   		
							} catch (Exception sqlE) {
								JOptionPane.showMessageDialog(null, "Record already exists...","Record Error", JOptionPane.INFORMATION_MESSAGE);
							}
					this.thisVehiclesAvailable.reloadRecord(false);
					dispose();

					}
					
					if (sqlCondition == "Edit") {
						  try {			  					
								Class.forName(driver);					
								Connection connection=DriverManager.getConnection(url);
						   		Statement statement = connection.createStatement();
				
						   		statement.execute("UPDATE Vehicles SET Capacity = "
						   				+ txtCapacity.getText() + " WHERE VehicleName ='"
						   				+ txtName.getText() + "'");
						   		connection.close();
						   		
							} catch (Exception sqlE) {
								JOptionPane.showMessageDialog(null, "Record already exists...","Record Error", JOptionPane.INFORMATION_MESSAGE);
							}
							
					this.thisVehiclesAvailable.reloadRecord(false);
					dispose();
				}
					
			}

		}

	}

}
