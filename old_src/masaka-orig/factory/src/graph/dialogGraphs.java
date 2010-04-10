package graph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import files.MainFrame;

public class dialogGraphs extends JDialog implements ActionListener{
	
	private Vector<Vector> vehiclesAvailable;
	private ResultSet rs;
	private Panel canvasp;
	private String driver="sun.jdbc.odbc.JdbcOdbcDriver";
	String url="jdbc:odbc:moses";
	public boolean chargesConditions = true;
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	public MainFrame main;
	private JButton btnAdd;
    private JDesktopPane desktop = new JDesktopPane();
	public Vector<JouneyDetails> collectionIfUsedThisDirection = new Vector<JouneyDetails>();

	public dialogGraphs(boolean condition, boolean closedRoadOrCarDamaged, JFrame OwnerForm, MainFrame me, boolean viewVarietions, Vector<Vector> townsWhoOrdered) {
		super(OwnerForm, true);
		this.main = me;
		setTitle( "The different variations in the journey depending on the direction taken");

		canvasp = new Panel();
		canvasp.setLayout(null);
		
		graphWork(2,2,condition,closedRoadOrCarDamaged, "vehiclesAscending","clockwise", townsWhoOrdered);
		graphWork(screen.width *11/25, 2,condition,closedRoadOrCarDamaged, "vehiclesDescending","clockwise", townsWhoOrdered);
		graphWork(screen.width *11/25, screen.height *21/50,condition,closedRoadOrCarDamaged, "vehiclesAscending", "anticlockwise", townsWhoOrdered);
		graphWork(2, screen.height *21/50,condition,closedRoadOrCarDamaged, "vehiclesDescending", "anticlockwise", townsWhoOrdered);

        btnAdd = new JButton ("Veiw Graph");
        btnAdd.setBounds ((screen.width*9/25), (screen.height*42/50), 100, 25);
        btnAdd.addActionListener (this);
		canvasp.add("Center", btnAdd);
        
		setLayout(new BorderLayout());
		getContentPane().add(canvasp, BorderLayout.CENTER);
		
		if(this.main.to.cumulativeForTown.size() != 0){
			
			   int theCheapestGraphCost = Integer.MAX_VALUE;		   
			   String directn = "";
			   String carDirection = "";
			      
			   for(int i =0;i<this.main.jouneyGotWithThisGraph.size();i++){
			   		if(theCheapestGraphCost > Integer.parseInt(this.main.jouneyGotWithThisGraph.get(i).get(2).toString())){
			   			theCheapestGraphCost = Integer.parseInt(this.main.jouneyGotWithThisGraph.get(i).get(2).toString());	
			   			directn = this.main.jouneyGotWithThisGraph.get(i).get(0).toString();
			   			carDirection = this.main.jouneyGotWithThisGraph.get(i).get(1).toString();
			   		}			
				}
			   
			  	try{
			  		Properties props = new Properties();
	     
			  		props.store(new FileOutputStream("C:/savedInfo.obj"),null);
			  	}catch(IOException e){System.out.println("savedInfo file not created    "+e.getMessage());}

		     	try {
		     	  	FileWriter fileOpen = new FileWriter( "C:/savedInfo.obj", true );
		      		PrintWriter cOut = new PrintWriter(new BufferedWriter( fileOpen ));
		      		
		      	    cOut.println( "'Direction'"+" '"+directn+"' '"+carDirection+"' "+theCheapestGraphCost);
		      
		      	    for(int z=0;z<this.main.to.cumulativeForTown.size();z++){
		     	 
		     	      cOut.println( "'order'"+" '"+this.main.to.cumulativeForTown.get(z).get(0).toString()+"' "+this.main.to.cumulativeForTown.get(z).get(1));
		     	 
		      		}
		     
		      		cOut.close();
		      
		      	}catch(IOException xp){
		      		System.out.println(xp.getMessage());
					JOptionPane.showMessageDialog(null, "Records have not been saved successfully...","Record Error", JOptionPane.INFORMATION_MESSAGE);
		      	}
	
		}

		if(viewVarietions == true){
			setSize((screen.width *22/25 ), (screen.height*23/25 ));
			setLocation(35, 35);
			setResizable(false);
			setVisible(true);
		}

	}

	private void graphWork(int locationWidth, int locationHeight, boolean condition,boolean closedRoadOrCarDamaged, String vehicleDirection, String direction, Vector<Vector> townsWhoOrdered) {

		vehiclesAvailable = new Vector<Vector>();

		try {
			
			Class.forName(driver);
	   		Connection connection=DriverManager.getConnection(url);
	   		Statement st = connection.createStatement();

			if (vehicleDirection.contentEquals("vehiclesAscending")) {
				rs = st.executeQuery("select * from Vehicles order by Capacity Asc");
			} else if (vehicleDirection.contentEquals("vehiclesDescending")) {
				rs = st.executeQuery("select * from Vehicles order by Capacity Desc");
			}

				while (rs.next()) {
					Vector<String> rowDataSingle = new Vector<String>();
					rowDataSingle.addElement(rs.getString("VehicleName"));
					rowDataSingle.addElement(rs.getString("Capacity"));
					rowDataSingle.addElement(rs.getString("ChargePerDay"));
					rowDataSingle.addElement(rs.getString("ChargesPerKm"));

					this.vehiclesAvailable.addElement(rowDataSingle);
				}
			
			connection.close();

		} catch (Exception ex) {System.out.println(ex);	}

		ClientsBefore c = new ClientsBefore(false, locationWidth, locationHeight, condition, closedRoadOrCarDamaged,this.main, direction, (screen.width * 10 / 25), (screen.height * 2/5), townsWhoOrdered,	this.vehiclesAvailable, vehicleDirection );

        int totalCost = 0;
        int labelCount = 0;
        
        for(int i =0; i< c.collectionIfUsedThisDirection.size(); i++){
        	
        	        String nodeNames = "";
 
         for(int j=0;j<c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.size();j++){
        	try{
        		if( c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j).contentEquals(c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j+1))){
        			for(int p=0; p<c.collectionIfUsedThisDirection.get(i).theNodesSupplied.size(); p++){
        				if(c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j).contentEquals(c.collectionIfUsedThisDirection.get(i).theNodesSupplied.get(p).name)){
        				nodeNames = nodeNames + c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j) + " ( "+  c.collectionIfUsedThisDirection.get(i).theNodesSupplied.get(p).OrderWanted +" ) ; ";
        				}
        			}
        		}else{
	       				if( c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j).contentEquals(c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j-1))){
	        			
	       				}else{
	       					nodeNames = nodeNames + c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j) + " ; ";
	       				}
	        	}
        	}catch(ArrayIndexOutOfBoundsException e){
					nodeNames = nodeNames + c.collectionIfUsedThisDirection.get(i).allPlacesToVisit.get(j) + " ; ";
        	}
         }
        	
        	String wholeString = c.collectionIfUsedThisDirection.get(i).carName+"     "+nodeNames+"     km ="+c.collectionIfUsedThisDirection.get(i).NoOfKmCovered+"    cost ="+c.collectionIfUsedThisDirection.get(i).jouneyCost;
        	int labelsForThisCar = ((wholeString).length()/75);
        	if(((wholeString).length()%75) != 0){
        		labelsForThisCar = labelsForThisCar + 1;
        	}
        	
    		JLabel[] daLabels = new JLabel[labelsForThisCar];

        	for(int k =0; k<labelsForThisCar; k++){
        		try{
        			daLabels[k] = new JLabel(wholeString.substring(75*k, 75*(k+1)));
        			daLabels[k].setBounds(locationWidth, locationHeight + (30*(labelCount + k)), (screen.width * 11 / 25), 25);
        			canvasp.add(daLabels[k]);
        		}catch(StringIndexOutOfBoundsException e){
        			daLabels[k] = new JLabel(wholeString.substring(75*k,(75*k)+(wholeString.length()%75)));
        			daLabels[k].setBounds(locationWidth, locationHeight + (30*(labelCount + k)), (screen.width * 11 / 25), 25);
        			canvasp.add(daLabels[k]);
        		}

        	}
        	
        labelCount	= labelCount + labelsForThisCar;
    	totalCost = totalCost + c.collectionIfUsedThisDirection.get(i).jouneyCost;	
	       
       }
       
        	JLabel joneyCost = new JLabel("Total Jouney Cost = "+totalCost);
        	joneyCost.setBounds (locationWidth,  locationHeight + (30*(labelCount + 1)), (screen.width * 10 / 25), 30);
           	canvasp.add(joneyCost);
		
		getContentPane().add(canvasp, BorderLayout.CENTER);
		this.vehiclesAvailable.removeAllElements();

	}

	public void actionPerformed (ActionEvent ae) {

	        Object obj = ae.getSource();
	        
	         if (obj == btnAdd){
	        	 this.main.kim1.graphDisplay();

				}
				
	     }
	  
}
