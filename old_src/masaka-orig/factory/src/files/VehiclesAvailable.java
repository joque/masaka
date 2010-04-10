package files;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class VehiclesAvailable extends JInternalFrame implements ActionListener {
	private int screenHeight;
	private int screenWidth;
	private Dimension screen;
	private static JPanel jpShow = new JPanel();
	private DefaultTableModel dtmCustomer;
	public static JTable tbCustomer;
	private static JScrollPane jspTable;
	private JDesktopPane desktop = new JDesktopPane();
	JFrame JFParentFrame;
	private static Vector<String> columnNames = new Vector<String>();
	public Vector<Vector> rowDataCollect = new Vector<Vector>();
	private JButton btnAdd, btnEdit, btnDelete, btnCharge;
	private String driver="sun.jdbc.odbc.JdbcOdbcDriver";
	String url="jdbc:odbc:moses";

     public	VehiclesAvailable(String whichOne, JFrame getParentFrame, Dimension screen) {
		super("List of Vehicles available", false, false, false, false);
		this.screen = screen;
		screenHeight = (screen.height / 4);
		screenWidth = (screen.width * 2 / 5);
		jpShow.setLayout(null);
		JFParentFrame = getParentFrame;

		btnAdd = new JButton("Add");
		btnAdd.setBounds((screenWidth * 15 / 20), (screenHeight * 1 / 10), 80, 25);
		btnAdd.addActionListener(this);
		btnEdit = new JButton("Edit");
		btnEdit.setBounds((screenWidth * 15 / 20),(screenHeight * 1 / 10) + 30, 80, 25);
		btnEdit.addActionListener(this);
		btnDelete = new JButton("Delete");
		btnDelete.setBounds((screenWidth * 15 / 20),(screenHeight * 1 / 10) + 60, 80, 25);
		btnDelete.addActionListener(this);
		btnCharge = new JButton("Charges");
		btnCharge.setBounds((screenWidth * 15 / 20), (screenHeight * 1 / 10)+100, 90, 25);
		btnCharge.addActionListener(this);

		jpShow.add(btnAdd);
		jpShow.add(btnEdit);
		jpShow.add(btnDelete);
		jpShow.add(btnCharge);

		columnNames.addElement("Vehicle Name");
		columnNames.addElement("Capacity");
		
        reloadRecord(true);

		jspTable = new JScrollPane(tbCustomer);
		jspTable.setBounds((screenWidth * 1 / 20), (screenHeight * 1 / 10),(screenWidth * 7 / 10), (screenHeight * 6 / 10));
		jpShow.add(jspTable);

		desktop.putClientProperty("JDesktopPane.dragMode", "outline");
		getContentPane().add(desktop, BorderLayout.CENTER);

		getContentPane().add(jpShow);
		setSize(screenWidth, screenHeight);
		setLocation((screen.width / 15), (screen.height * 1 / 22));
		setVisible(true);
	}

	public void actionPerformed(ActionEvent ae) {

		Object obj = ae.getSource();

		if (obj == btnAdd) {
			JDialog viewCell = new dialogVehicle(this, "Register New Vehicle",JFParentFrame, "", "", "New", this.screen);
			try {
				desktop.add(viewCell);
			} catch (IllegalArgumentException e) {
			}
		}
		
		if (obj == btnEdit) {
			try {
				JDialog viewCel = new dialogVehicle(this, "Edit Vehicle Capacity",JFParentFrame, tbCustomer.getValueAt(tbCustomer.getSelectedRow(), 0).toString(),tbCustomer.getValueAt(tbCustomer.getSelectedRow(), 1).toString(), "Edit", this.screen);
				try {
					desktop.add(viewCel);
				} catch (IllegalArgumentException e) {
				}

			} catch (ArrayIndexOutOfBoundsException sqlE) {
				JOptionPane.showMessageDialog(
								null,
								"No Records have been selected for Editing, Please Select one first",
								"No Record Selected",
								JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
	    if (obj == btnCharge) {
	        
			JDialog viewQty = new dialogCharges("Please enter the charges of the vehicles",JFParentFrame);
			try{
			desktop.add( viewQty );
			}catch(IllegalArgumentException e){}
        }
	    
		if (obj == btnDelete) {
			try {
				Class.forName(driver);					
				Connection connection=DriverManager.getConnection(url);
		   		Statement statement = connection.createStatement();

					String ObjButtons[] = { "Yes", "No" };
					int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure you want to delete vehicle "+ tbCustomer.getValueAt(tbCustomer.getSelectedRow(), 0)+ " and all its services?","Delete Record", JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE, null, ObjButtons,ObjButtons[1]);
					if (PromptResult == 0) {
						statement.execute("DELETE  FROM Vehicles WHERE vehicleName = '"+ tbCustomer.getValueAt(tbCustomer.getSelectedRow(), 0) + "'");
						connection.close();
						reloadRecord(false);

					}
				
			} catch (Exception e) {
				System.out.println("kim   "+e.getMessage());
				JOptionPane.showMessageDialog(null,"No Records have been selected for deleting, Please Select one first","No Record Selected",JOptionPane.INFORMATION_MESSAGE);
			}
		}

	}
   
	 public void reloadRecord(boolean firstTime){
			
			if(firstTime == false){
			jspTable.getViewport().removeAll();
			}
			Vector<Vector> clientsAll = new Vector<Vector>();
			
			  try
			  {			  					
					Class.forName(driver);					
					Connection connection=DriverManager.getConnection(url);
			   		Statement statement = connection.createStatement();	
					ResultSet rs = statement.executeQuery("select * from Vehicles order by Capacity Asc");
					
					while (rs.next()) {
						Vector<String> rowDataSingle = new Vector<String>();
						rowDataSingle.addElement(rs.getString("VehicleName"));
						rowDataSingle.addElement(rs.getString("Capacity"));

						clientsAll.addElement(rowDataSingle);
					}

					dtmCustomer = new DefaultTableModel(clientsAll, columnNames);
					connection.close();	
			  }
			  catch(Exception ex){
			  		//
			  }
	 
	           tbCustomer = new JTable( dtmCustomer )
	           {
	               public boolean isCellEditable(int iRow, int iCol)
	               {
	                   return false; 
	               }
	           };
	           (tbCustomer.getColumnModel().getColumn( 0 )).setPreferredWidth( 250 );
	           (tbCustomer.getColumnModel().getColumn( 1 )).setPreferredWidth( 150 );

	           tbCustomer.setRowHeight( 20 );
	           tbCustomer.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

	   		if(firstTime == false){
	   		jspTable.getViewport().add(tbCustomer);
			jpShow.repaint();

	   		}
	   				
	}

}
