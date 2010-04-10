package files;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

public class ClientDetails extends JInternalFrame implements ActionListener
{
	Dimension screen = 	Toolkit.getDefaultToolkit().getScreenSize();
    private static JPanel jpShow = new JPanel();
    private DefaultTableModel dtmCustomer ;
    private static JTable tbCustomer;
    private static JScrollPane jspTable;
    private JDesktopPane desktop = new JDesktopPane();
	JFrame JFParentFrame;
    private ResultSet rs;
    private static Connection connection;

    private static Vector<String> columnNames = new Vector<String>();
    private Vector<Vector> rowDataCollect = new Vector<Vector>();
    private JButton btnAdd, btnEdit,btnDelete,btnSelect;
    
    private String serverName = "localhost";
    private String mydatabase = "studentProject";
    private String url = "jdbc:mysql://" + serverName + "/" + mydatabase; // a JDBC url 
    private String username = "root";
    private String password = "test";
    private static String driverName = "org.gjt.mm.mysql.Driver"; // MySQL MM JDBC driver 

    ClientDetails(String whichOne,JFrame getParentFrame)
    {
        super( "All Car Brands Accomodated", false, true, false, true );
       jpShow.setLayout( null );
		JFParentFrame = getParentFrame;
		
       buttonsToShow(whichOne);
       
        try {
            Class.forName( driverName ).newInstance();
 
            connection = DriverManager.getConnection( url, username, password );

            Statement st = connection.createStatement();
            rs = st.executeQuery( "select * from clientInfo" );
            columnNames.addElement("No");
            columnNames.addElement("Car Brand Name");
            
            try {
                while ( rs.next() ) {
                	Vector<String> rowDataSingle = new Vector<String>();
                	rowDataSingle.addElement(rs.getString( 1 ));
                	rowDataSingle.addElement(rs.getString( 2 ));
                	
                	rowDataCollect.addElement(rowDataSingle);
                }
            }
            catch ( Exception e ) {System.out.println(e);}
            
          dtmCustomer = new DefaultTableModel(rowDataCollect, columnNames); 
        }
        catch ( Exception ex ) {System.out.println(ex);}
        
       tbCustomer = new JTable( dtmCustomer )
        {
            public boolean isCellEditable(int iRow, int iCol)
            {
                return false; //Disable All Columns of Table.
            }
        };
        
        (tbCustomer.getColumnModel().getColumn( 0 )).setPreferredWidth( 50 );
        (tbCustomer.getColumnModel().getColumn( 1 )).setPreferredWidth( 350 );

        tbCustomer.setRowHeight( 20 );
        tbCustomer.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        jspTable = new JScrollPane( tbCustomer );
        jspTable.setBounds( 20, 20, tbCustomer.getColumnModel().getTotalColumnWidth()+100, 200 );
        jpShow.add( jspTable );
        
        desktop.putClientProperty( "JDesktopPane.dragMode", "outline" );
        getContentPane().add( desktop, BorderLayout.CENTER );
        
        getContentPane().add( jpShow );
        setSize(300, 330 );
        setLocation((screen.width - 325)/3,((screen.height-383)/3));
        setVisible( true );
   }

     public void actionPerformed (ActionEvent ae) {

        Object obj = ae.getSource();

        if (obj == btnDelete){
			try{
			        Statement btnSt = connection.createStatement();

					if( tbCustomer.getValueAt(tbCustomer.getSelectedRow(),tbCustomer.getSelectedColumn())!= null){
						String ObjButtons[] = {"Yes","No"};
						int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure you want to delete all records on "+tbCustomer.getValueAt(tbCustomer.getSelectedRow(),tbCustomer.getSelectedColumn())+" and all its CLASSES?" ,"Delete Record",JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE,null,ObjButtons,ObjButtons[1]);
						if(PromptResult==0){
							btnSt.execute("DELETE  FROM car_type WHERE Type_Name = '" + tbCustomer.getValueAt(tbCustomer.getSelectedRow(),tbCustomer.getSelectedColumn())+"'");
							CarBrands.reloadRecord();
							JOptionPane.showMessageDialog(null,tbCustomer.getValueAt(tbCustomer.getSelectedRow(),tbCustomer.getSelectedColumn())+" Records have all been successfully deleted.","Comfirm Delete",JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}catch(Exception sqlE){
						JOptionPane.showMessageDialog(null,"No Records have been selected for deleting, Pliz Select one first","No Record Selected",JOptionPane.INFORMATION_MESSAGE);
					}
				}
 
     }
     
 	public static void reloadRecord(){
 		Vector<Vector> rowDataCollections = new Vector<Vector>();
		jspTable.getViewport().removeAll();
        try {
            Class.forName( driverName ).newInstance();
 
           Statement st = connection.createStatement();
           ResultSet  rs2 = st.executeQuery( "select * from Car_Type" );
           while ( rs2.next() ) {
           	Vector<String> rowDataSingle = new Vector<String>();
           	rowDataSingle.addElement(rs2.getString( 1 ));
           	rowDataSingle.addElement(rs2.getString( 2 ));
           	
           	rowDataCollections.addElement(rowDataSingle);
           }
           DefaultTableModel dtmCustomer2 = new DefaultTableModel(rowDataCollections, columnNames); 
       
           tbCustomer = new JTable( dtmCustomer2 )
           {
               public boolean isCellEditable(int iRow, int iCol)
               {
                   return false; //Disable All Columns of Table.
               }
           };
           (tbCustomer.getColumnModel().getColumn( 0 )).setPreferredWidth( 50 );
           (tbCustomer.getColumnModel().getColumn( 1 )).setPreferredWidth( 350 );
           tbCustomer.setRowHeight( 20 );
           tbCustomer.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        }catch (Exception x){System.out.println(x);}
		jspTable.getViewport().add(tbCustomer);

		jpShow.repaint();
}
 
    private void buttonsToShow(String whichOne){
    	
    	if(whichOne == "CarBrands"){
        btnAdd = new JButton ("Add Car");
        btnAdd.setBounds (5, 245, 80, 25);
        btnAdd.addActionListener (this);
        btnEdit = new JButton ("Edit Car");
        btnEdit.setBounds (100, 245, 80, 25);
        btnEdit.addActionListener (this);
        btnDelete = new JButton ("Delete");
        btnDelete.setBounds (195, 245, 85, 25);
        btnDelete.addActionListener (this);
        
        jpShow.add (btnAdd);
        jpShow.add (btnEdit);
        jpShow.add (btnDelete);
    	}
    	else if(whichOne == "CarBrandGroups"){
            btnSelect = new JButton ("Select Car Class");
            btnSelect.setBounds (77, 245, 130, 25);
            btnSelect.addActionListener (this);
            
            jpShow.add (btnSelect);
    	} 
    }
}
