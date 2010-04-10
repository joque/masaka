package files;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class dialogCharges extends JDialog implements ActionListener{
	
	  private JPanel jpShow = new JPanel();
	  private JButton btnSet;
	  private JTextField[] txtStandardCharge,txtFuelCharge;
	  private JLabel[] daLabels;
	private Vector<Vector> vehiclesAvailable;
	private String driver="sun.jdbc.odbc.JdbcOdbcDriver";
	String url="jdbc:odbc:moses";
	Dimension screen = 	Toolkit.getDefaultToolkit().getScreenSize();
	  
	public dialogCharges(String titttle,JFrame OwnerForm){
		super(OwnerForm,true);
		setTitle(titttle);	
       jpShow.setLayout( null );
       vehiclesAvailable = new Vector<Vector>();
       
		  try {			  					
				Class.forName(driver);					
				Connection connection=DriverManager.getConnection(url);
		   		Statement statement = connection.createStatement();	
				ResultSet rs = statement.executeQuery("select * from Vehicles order by Capacity Asc");

                while ( rs.next() ) {
                	Vector<String> rowDataSingle = new Vector<String>();
                	rowDataSingle.addElement(rs.getString("VehicleName"));
                	rowDataSingle.addElement(rs.getString("Capacity"));
                	rowDataSingle.addElement(rs.getString("ChargePerDay"));
                	rowDataSingle.addElement(rs.getString("ChargesPerKm"));
                	
                	this.vehiclesAvailable.addElement(rowDataSingle);
                }
                
                connection.close(); 
                
            } catch ( Exception e ) {System.out.println(e);}
        
       daLabels = new JLabel[vehiclesAvailable.size()];

        JLabel aLabels = new JLabel("Charges/day");
        aLabels.setBounds (115, 2, 85, 15);
    	jpShow.add(aLabels);
    	JLabel aLabel = new JLabel("Charges/km");
        aLabel.setBounds (235, 2, 105, 15);
    	jpShow.add(aLabel);
    	
       txtStandardCharge = new JTextField[vehiclesAvailable.size()];
       txtFuelCharge = new JTextField[vehiclesAvailable.size()];
       
       for(int i =0;i<this.vehiclesAvailable.size();i++){
          	daLabels[i] = new JLabel(vehiclesAvailable.get(i).get(0).toString());
          	txtStandardCharge[i] = new JTextField();
          	txtFuelCharge[i] = new JTextField();
          	daLabels[i].setBounds (25, ((35*i)+20), 115, 25);
    		jpShow.add(daLabels[i]);
    		
    	       txtStandardCharge[i].setHorizontalAlignment (JTextField.RIGHT);

    	       txtStandardCharge[i].setBounds (115, ((35*i)+20), 100, 25);
    	       txtFuelCharge[i].setHorizontalAlignment (JTextField.RIGHT);
    	       txtFuelCharge[i].setBounds (235, ((35*i)+20), 100, 25);
    	          txtStandardCharge[i].setText(vehiclesAvailable.get(i).get(2).toString());
    	          txtStandardCharge[i].addKeyListener (new KeyAdapter() {
            	public void keyTyped (KeyEvent ke) {
                char c = ke.getKeyChar ();
                if (!((Character.isDigit (c) || (c == KeyEvent.VK_BACK_SPACE)))) {
                    getToolkit().beep ();
                    ke.consume ();
                    }
                }
        }
        );
    	          	
        	      txtFuelCharge[i].setText(vehiclesAvailable.get(i).get(3).toString());
        	      txtFuelCharge[i].addKeyListener (new KeyAdapter() {
            	public void keyTyped (KeyEvent ke) {
                char c = ke.getKeyChar ();
                if (!((Character.isDigit (c) || (c == KeyEvent.VK_BACK_SPACE)))) {
                    getToolkit().beep ();
                    ke.consume ();
                    }
                }
        }
        );
        	
	           	  jpShow.add (txtFuelCharge[i]);
    	       
    	       
    	       jpShow.add (txtStandardCharge[i]);
    	       
       }
       
       btnSet = new JButton ("Update their Costs");
       btnSet.setBounds (115, ((35*vehiclesAvailable.size())+20), 150, 25);
       btnSet.addActionListener (this);
       jpShow.add (btnSet);
       
      
       getContentPane().add( jpShow );
       setSize((screen.width*2/5), ((35*(vehiclesAvailable.size()+2))+20));
	   setResizable(false);
	   setLocation((screen.width - 325)/2,((screen.height*1)/8));
       setVisible( true );    
       
    }
	
    public void actionPerformed (ActionEvent ae) {

        Object obj = ae.getSource();

        if (obj == btnSet){

        	for(int i =0;i<this.vehiclesAvailable.size();i++){
				  try {			  					
						Class.forName(driver);					
						Connection connection=DriverManager.getConnection(url);
				   		Statement statement = connection.createStatement();
		
				   		statement.execute("UPDATE Vehicles SET ChargePerDay = "+ txtStandardCharge[i].getText() +", ChargesPerKm ="+ txtFuelCharge[i].getText() +" WHERE VehicleName ='"+ daLabels[i].getText()+"'");
				   		connection.close();
				   		
				  }catch(Exception ex){System.out.println(ex);}
				
        	}
        	
     //   	JOptionPane.showMessageDialog(null,"All records have been successfully Updated","Set Costs and Charges",JOptionPane.INFORMATION_MESSAGE);
						dispose();
					}
        }
        
}
