package files;

import graph.CompGeomTest;
import graph.GraphMap;

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

public class ClientList extends JInternalFrame implements ActionListener{
	private int screenHeight;
	private int screenWidth;
    private static JPanel jpShow = new JPanel();
    private DefaultTableModel dtmCustomer ;
    public static JTable tbCustomer;
    private static JScrollPane jspTable;
    private JDesktopPane desktop = new JDesktopPane();
	JFrame JFParentFrame;
    private static Vector<String> columnNames = new Vector<String>();
    private JButton btnAdd, btnDelete;
	private String driver="sun.jdbc.odbc.JdbcOdbcDriver";
	String url="jdbc:odbc:moses";

  public ClientList(String whichOne,JFrame getParentFrame, Dimension screen){
        super( "List of all Clients", false, false, false, false );
    	screenHeight = (screen.height/2);
    	screenWidth = (screen.width/4);
    	jpShow.setLayout( null );
		JFParentFrame = getParentFrame;
		
        btnAdd = new JButton ("New");
        btnAdd.setBounds ((screenWidth*2/25)+5,  (screenHeight*41/50), 85, 25);
        btnAdd.addActionListener (this);
     //   btnEdit = new JButton ("Edit");
     //   btnEdit.setBounds ((screenWidth*2/25)+90,  (screenHeight*41/50), 85, 25);
     //   btnEdit.addActionListener (this);
        btnDelete = new JButton ("Delete");
        btnDelete.setBounds ((screenWidth*2/25)+110,  (screenHeight*41/50), 85, 25);
        btnDelete.addActionListener (this);
        
        jpShow.add (btnAdd);
       // jpShow.add (btnEdit);
        jpShow.add (btnDelete);
        
        columnNames.addElement("Client Names");
        columnNames.addElement("Town");

         reloadRecord(true);

        jspTable = new JScrollPane( tbCustomer );
        jspTable.setBounds( (screenWidth*2/25), (screenHeight*2/25),(screenWidth*8/10), (screenHeight*7/10));
        jpShow.add( jspTable );
        
        desktop.putClientProperty( "JDesktopPane.dragMode", "outline" );
        getContentPane().add( desktop, BorderLayout.CENTER );
        
        getContentPane().add( jpShow );
        setSize(screenWidth,screenHeight );
        setLocation((screen.width/21),(screen.height*15/44));
        setVisible( true );
        
   }

  public void actionPerformed (ActionEvent ae) {

        Object obj = ae.getSource();
        
         if (obj == btnAdd){

			JDialog viewCus = new CompGeomTest( true, JFParentFrame, this);  
			try{
			desktop.add(viewCus);
			}catch(IllegalArgumentException e){}

			}

         
 		if (obj == btnDelete) {
			try {
				Class.forName(driver);
		   		Connection connection=DriverManager.getConnection(url);
				Statement btnSt = connection.createStatement();

				if (tbCustomer.getValueAt(tbCustomer.getSelectedRow(), 0) != null) {
					String ObjButtons[] = { "Yes", "No" };
					int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure you want to delete Client "+ tbCustomer.getValueAt(tbCustomer.getSelectedRow(), 0)+ " From the List?","Delete Record", JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE, null, ObjButtons,ObjButtons[1]);
					if (PromptResult == 0) {
						btnSt.execute("DELETE  FROM clientList WHERE name = '"+ tbCustomer.getValueAt(tbCustomer.getSelectedRow(), 0) + "' and town = '" + tbCustomer.getValueAt(tbCustomer.getSelectedRow(), 1) + "'" );
						connection.close();
						reloadRecord(false);

					}
				}
			} catch (Exception e) {
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
				String query = "select * from clientList order by id Asc";
				ResultSet rs = statement.executeQuery(query);
					
				while (rs.next()) {
					Vector<String> rowDataSingle = new Vector<String>();
					rowDataSingle.addElement(rs.getString("name"));
					rowDataSingle.addElement(rs.getString("town"));

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
           (tbCustomer.getColumnModel().getColumn( 1 )).setPreferredWidth( 170 );

           tbCustomer.setRowHeight( 20 );
           tbCustomer.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

   		if(firstTime == false){
   		jspTable.getViewport().add(tbCustomer);
		jpShow.repaint();

   		}
   				
}

 	public void graphDisplay(){
		JDialog viewCus = new GraphMap(JFParentFrame);  
		try{
		desktop.add(viewCus);
		}catch(IllegalArgumentException e){}
 	}
 	
}
